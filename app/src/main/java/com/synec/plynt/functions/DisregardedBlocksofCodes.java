package com.synec.plynt.functions;

public class DisregardedBlocksofCodes {





    //A 1 WAY LOOP THAT CHECKS TO ALL THE REACTION VIEWS IN EACH POST
//        String[] reactionsList = {"upvote", "downvote", "bookmark"};
//        ImageView[] holderIconList = {holder.upvoteIcon, holder.downvoteIcon, holder.bookmarkIcon};
//        int[] markedIconResource = {R.drawable.icon_upvote_yellow, R.drawable.icon_downvote_yellow, R.drawable.icon_bookmark_marked};
//        int[] unmarkedIconResource = {R.drawable.icon_upvote, R.drawable.icon_downvote, R.drawable.icon_bookmark_unmarked};

//// Loop through each reaction type in the reactionsList
//        for (int i = 0; i < reactionsList.length; i++) {
//            String collectionName = "Reactions"; // Optionally adjust this if collections are separate per reaction type.
//            int unmarkResource = unmarkedIconResource[i];
//            int markedResource = markedIconResource[i];
//            ImageView currentIcon = holderIconList[i];
//
//            _Master.isDocumentExistsAccordingToTwoParameters(
//                    collectionName,
//                    "user_document_id",
//                    "article_id",
//                    _Master.sharedPreferences.getString("session_user_id", ""),
//                    newsItem.getArticle_id(),
//                    currentIcon,
//                    markedResource,
//                    documentId -> {
//                        if (documentId == null) {
//                            Log.d(TAG, "storeReactionsInFirestore: No document. Setting to unmarked for " + collectionName);
//                            currentIcon.setImageResource(unmarkResource); // Set unmarked icon
//                        } else {
//                            Log.d(TAG, "storeReactionsInFirestore: Document exists for " + collectionName + " with ID: " + documentId);
//                            currentIcon.setImageResource(markedResource); // Set marked icon
//                        }
//                    }
//            );
//        }

}
