/*
 * Approvals-Java - Approval testing library for Java. Alleviates the burden of hand-writing assertions.
 * Copyright © 2018 Write Them First!
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.writethemfirst.approvals;

import com.github.writethemfirst.approvals.reporters.ThrowsReporter;
import com.github.writethemfirst.approvals.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ApprovalsFolderTest {
    private Approvals approvals = new Approvals(new ThrowsReporter());
    private ApprovalsFiles approvalsFiles = new ApprovalsFiles();
    private Path sample = Paths.get("sample.xml");
    private Path sample2 = Paths.get("sample2.xml");
    Reporter reporter = mock(Reporter.class);


    @Test
    void shouldDoNothingWhenBothFoldersAreEmpty() throws IOException {
        //GIVEN
        Files.createDirectories(approvalsFiles.defaultContext().approvedFolder());
        Path parent = Files.createTempDirectory("shouldDoNothingWhenBothFoldersAreEmpty");

        //WHEN
        approvals.verifyAgainstMasterFolder(parent);

        //THEN no exception should be thrown

    }

    @Test
    void shouldThrowWhenAFileIsMissing() throws IOException {
        Path parent = Files.createTempDirectory("shouldThrowWhenAFileIsMissing");

        assertThatThrownBy(() -> approvals.verifyAgainstMasterFolder(parent))
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("missing file <sample.xml>");

    }

    @Test
    void shouldThrowWhenAFileIsDifferent() throws IOException {
        Path parent = Files.createTempDirectory("shouldThrowWhenAFileIsDifferent");
        Files.createFile(parent.resolve("sample.xml"));
        assertThatThrownBy(() -> approvals.verifyAgainstMasterFolder(parent))
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("expected: <expected content> but was: <>");

    }

    @Test
    void shouldFireReporterOnEachMismatch() throws IOException {
        Approvals approvals = new Approvals(reporter);

        Path parent = Files.createTempDirectory("shouldFireReporterOnEachMismatch");
        Files.createFile(parent.resolve(sample));
        Files.createFile(parent.resolve(sample2));
        ApprobationContext context = approvalsFiles.defaultContext();

        try {
            approvals.verifyAgainstMasterFolder(parent);
        } catch (AssertionError e) {
            // expected
        }

        then(reporter).should().mismatch(context.approvedFile(sample), context.receivedFile(sample));
        then(reporter).should().mismatch(context.approvedFile(sample2), context.receivedFile(sample2));

        context.removeReceived(sample);
        context.removeReceived(sample2);
    }

    @Test
    void shouldCreateAllReceivedFiles() throws IOException {
        Approvals approvals = new Approvals(reporter);

        Path parent = Files.createTempDirectory("shouldCreateAllReceivedFiles");
        FileUtils.write("actual", parent.resolve("sample.xml"));
        FileUtils.write("actual2", parent.resolve("sample2.xml"));
        ApprobationContext context = approvalsFiles.defaultContext();

        try {
            approvals.verifyAgainstMasterFolder(parent);
        } catch (AssertionError e) {
            // expected
        }

        assertThat(context.receivedFile(sample)).hasContent("actual");
        assertThat(context.receivedFile(sample2)).hasContent("actual2");

        context.removeReceived(sample);
        context.removeReceived(sample2);
    }


    @Test
    void shouldRemoveMatchedReceivedFiles() throws IOException {
        Approvals approvals = new Approvals(reporter);

        Path parent = Files.createTempDirectory("shouldRemoveMatchedReceivedFiles");
        FileUtils.write("actual", parent.resolve("sample.xml"));
        ApprobationContext context = approvalsFiles.defaultContext();
        context.writeApproved("actual", sample);
        context.writeReceived("actual", sample);

        approvals.verifyAgainstMasterFolder(parent);

        assertThat(context.receivedFile(sample)).doesNotExist();

        context.removeReceived(sample);
        context.removeApproved(sample);
    }
}
