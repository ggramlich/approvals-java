package org.demo.simple;

import com.github.writethemfirst.approvals.Approvals;
import org.junit.jupiter.api.Test;


class ApproveString {
    private Approvals approvals = new Approvals();

    @Test
    void verifySimpleString() {
        approvals.verify("my string");
    }
}
