package com.example.demo.util;

import java.util.Arrays;
import java.util.List;

public class DocumentLoader {
    public static List<String> loadSampleDocuments() {
        return Arrays.asList(
                "Payment Decline Policy: Payments can be declined due to insufficient funds, incorrect card details, or bank restrictions.",
                "Troubleshooting Payment Issues: Check your balance, verify card number and expiry, and contact your bank if the issue persists.",
                "Common Decline Reasons: 1) Insufficient funds 2) Card expired 3) Incorrect CVV 4) Bank declined due to suspected fraud.",
                "Account Services: You can update your profile, change password, and manage preferences in the account settings."
        );
    }
}