package org.demo.simple;

import com.github.writethemfirst.approvals.Approvals;
import com.github.writethemfirst.approvals.reporters.JUnit5Reporter;
import org.junit.jupiter.api.Test;

import static com.github.writethemfirst.approvals.reporters.softwares.Windows.*;


class ApproveString {
    // You would not use that many approvers, this is just to make testing easy.
    private Approvals approvals = new Approvals(DEFAULT, new JUnit5Reporter());

    @Test
    void verifySimpleString() throws Throwable {
        approvals.verify("my string");
    }
}
