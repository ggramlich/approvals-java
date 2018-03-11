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

import com.github.writethemfirst.approvals.reporters.FirstWorkingReporter;
import com.github.writethemfirst.approvals.reporters.JUnit5Reporter;
import com.github.writethemfirst.approvals.reporters.ThrowsReporter;
import com.github.writethemfirst.approvals.reporters.windows.Windows;
import com.github.writethemfirst.approvals.utils.OSUtils;

import java.nio.file.Path;

/**
 * # Reporter
 *
 * *Approvals-Java*, as many other *Approval Testing* frameworks, relies on comparisons between data which has been
 * approved by the developer, and data which is computed from the *Program Under Tests*.
 *
 * In case there's any difference found during the comparison, the framework needs to be able to report those
 * differences to the developer. There are plenty of ways to report those differences, like generating assertions
 * errors, logging the differences in a file, or whatever you can imagine.
 *
 * The `Reporter` interface is the contract used by the framework to report the differences found during computing the
 * comparisons.
 *
 * There will be a set of reporters already implemented and provided by the framework, but people can implement their
 * owns and  use them while computing the approvals.
 *
 * @author mdaviot / aneveux
 * @version 1.0
 */
public interface Reporter {

    /**
     * Global property allowing to retrieve the default reporter for the current execution context.
     *
     * Careful, that default reporter is completely dependent on the execution context (OS, classpath, etc.)
     */
    Reporter DEFAULT = OSUtils.isWindows
        ? Windows.DEFAULT
        : new FirstWorkingReporter(new JUnit5Reporter(), new ThrowsReporter());

    /**
     * A `Reporter` is called whenever a difference is found while comparing the output of a *Program Under Tests* and
     * the existing *approved* files linked to the tests. That method is the one which will be called by the framework,
     * specifying the *approved* and *received* files which present differences.
     *
     * It is then used to implement the behavior to execute for dealing with that mismatch between both files.
     *
     * @param approved The {@link Path} to the *approved* file (which  contains the data reviewed and approved by the
     *                 developer)
     * @param received The {@link Path} to the temporary *received* file (which contains the data generated by the
     *                 *Program Under Tests*)
     * @throws AssertionError   if the implementation relies on standard assertions provided by a framework like JUnit
     * @throws RuntimeException if it relies on executing an external command which failed
     */
    void mismatch(Path approved, Path received);

    /**
     * A `Reporter` may not be relevant in all contexts, and they should provide some automated checks validating that
     * they're actually able to perform what they're supposed to do in the current execution customFiles (like libraries
     * available in the classpath or Diff / Merge programs available on the computer).
     *
     * This method allows to state if a `Reporter` should be executed in case any differences are found during the
     * comparison of *approved* and *received* files. If it returns false, the `Reporter` won't be used.
     *
     * @return true if the `Reporter` can be used in the current execution customFiles.
     */
    boolean isAvailable();

}
