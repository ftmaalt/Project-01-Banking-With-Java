package com.ga.acmebank.account;

import java.io.IOException;
import java.nio.file.Path;

public class OverdraftActions {
        public static final double OverDraft_Fee= 35.0;
        public static final int Max_allowed= 2;

        private boolean deactivated=false;


        public double applyOverDraftFree(String userId, String secHeader, double negAmount) throws IOException {
                Path file= AccountFileHelper.findFileFromId(userId).orElseThrow(()-> new IOException("File not found"));
                double balanceAfterFee = negAmount - OverDraft_Fee;

                int count = AccountFileHelper.getOverDraftCount(file, secHeader)+1;
                AccountFileHelper.updateOverDraftCount(file, secHeader, count);

                deactivated=false;
                if (count>=Max_allowed){
                        AccountFileHelper.accountStatus(file, secHeader, "DEACTIVATED");
                        deactivated=true;
                }
                return balanceAfterFee;
        }

        public boolean isDeactivated(String userID, String sectionHeader) throws IOException {
                Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()-> new IOException("File not found"));

                return AccountFileHelper.isDeactivated(file, sectionHeader);
        }

        public boolean reactivateResolved(String userID, String sectionHeader, double balance) throws IOException {
                if (balance>=0){
                        Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()-> new IOException("File not found"));
                        AccountFileHelper.accountStatus(file, sectionHeader,"ACTIVE");
                        AccountFileHelper.updateOverDraftCount(file, sectionHeader, 0);
                        return true;
                }
                return false;
        }
}
