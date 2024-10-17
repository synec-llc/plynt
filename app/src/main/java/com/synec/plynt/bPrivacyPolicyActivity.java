package com.synec.plynt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class bPrivacyPolicyActivity extends AppCompatActivity {

    private static String TAG = "bPrivacyPolicyActivity";
    // Declare all views
    private ConstraintLayout termsAndConditionsContainer;
    private ImageView termsAndConditionsImageView;
    private Button confirmButton;
    private boolean isTermsAccepted = false;  // Track whether the terms are accepted or not


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bprivacy_policy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the views
        termsAndConditionsContainer = findViewById(R.id.termsAndCoditionsContainer);
        termsAndConditionsImageView = findViewById(R.id.termsAndConditionsImageView);
        confirmButton = findViewById(R.id.logInButton);

        populatePrivacyTextView();
        _Master.showAllSharedPreferences(bPrivacyPolicyActivity.this, TAG);

        // Set click listener on the termsAndConditionsContainer layout
        termsAndConditionsContainer.setOnClickListener(v -> {
            if (isTermsAccepted) {
                // If already checked, uncheck it
                termsAndConditionsImageView.setImageResource(R.drawable.icon_checkbox_unchecked);
            } else {
                // If unchecked, check it
                termsAndConditionsImageView.setImageResource(R.drawable.icon_checkbox_checked);
            }
            isTermsAccepted = !isTermsAccepted;  // Toggle the state
        });

        // Set click listener on the confirm button
        confirmButton.setOnClickListener(v -> {
            if (isTermsAccepted) {
                _Master.updateFirestoreWithDocumentID(bPrivacyPolicyActivity.this, "UserData", _Master.sharedPreferences.getString("session_user_document_id", null), "agreed_to_privacy_policy_date", _Master.sdf.format(new Date()));

                Log.d(TAG, "Privacy policy date updated successfully for " + _Master.sharedPreferences.getString("session_full_name", null));
                Toast.makeText(bPrivacyPolicyActivity.this, "Privacy policy date updated successfully.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(bPrivacyPolicyActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                // If terms are not accepted, show a toast
                Toast.makeText(bPrivacyPolicyActivity.this, "You must accept the terms and conditions to proceed.", Toast.LENGTH_SHORT).show();
            }
        });
    }








    private void populatePrivacyTextView() {
        TextView privacyPolicyTextView = findViewById(R.id.privacyPolicyText);
        String privacyPolicy =
                "Effective Date: October 15, 2024\n"
                        + "Last Updated: October 15, 2024\n\n"
                        + "Thank you for choosing Plynt, a product of Synec Software Development Services (\"Synec,\" \"we,\" \"our,\" or \"us\"). "
                        + "Your privacy is of paramount importance to us, and we are fully committed to ensuring that your personal information "
                        + "is handled with care, confidentiality, and security. This Privacy Policy and Terms of Service (collectively, \"Policy\") "
                        + "outlines how we collect, use, and protect the information we gather from users of our platform (\"Plynt\" or the \"App\"). "
                        + "By using Plynt, you acknowledge and agree to the terms set forth below.\n\n"
                        + "This Policy applies to all users (\"you\" or \"users\") of the Plynt platform, including the Plynt mobile application, website, "
                        + "and related services. If you do not agree to these terms, please do not use the services.\n\n"
                        + "1. Acceptance of Terms\n"
                        + "By accessing or using Plynt, you agree to be bound by this Privacy Policy and Terms of Service. Your continued use of the App "
                        + "signifies your acceptance of any changes or modifications to the Policy, which may be updated at our discretion. We will notify "
                        + "users of significant changes via email or through in-app notifications. It is your responsibility to review these terms periodically "
                        + "to stay informed.\n\n"
                        + "2. Information We Collect\n"
                        + "To provide you with personalized news content and features within Plynt, we collect the following personal information, depending "
                        + "on your method of signing up:\n"
                        + "• Email and Password: If you sign up using an email and password, we collect your email address and use it to create your account, "
                        + "manage your login credentials, and communicate with you for service-related purposes.\n"
                        + "• Facebook or Google Sign-Up: If you choose to sign up using Facebook or Google, we collect your email address, profile photo, "
                        + "first name, and last name from your associated social media or Google account. This information is used to create and personalize "
                        + "your profile within Plynt and to facilitate your login.\n"
                        + "We do not collect any unnecessary data, and we do not have access to your social media passwords. Plynt does not sell, rent, or "
                        + "share your personal information with third-party advertisers or companies.\n\n"
                        + "3. Location and Other Permissions\n"
                        + "Plynt may request access to certain device features and permissions in order to function properly. These permissions include but "
                        + "are not limited to:\n"
                        + "• Location: Plynt may request access to your location in order to provide localized news content and features, such as nearby news "
                        + "updates or weather alerts. Location data will only be collected if you grant permission and will only be used when necessary for "
                        + "specific features.\n"
                        + "• Storage: We may request access to your device's storage to allow you to download and save articles or other content for offline "
                        + "use. This data is stored locally on your device and not uploaded to our servers.\n"
                        + "• Microphone: Plynt may request access to your microphone if you choose to interact with voice features, such as voice commands "
                        + "or voice-over functionalities. This access will only be active while using the voice features and will not be used for any other "
                        + "purpose.\n"
                        + "• Other Permissions: Depending on future updates or functionalities, we may request access to other features, such as camera or "
                        + "notifications, to enhance your experience. You will have the ability to accept or decline these permissions, and all permissions "
                        + "can be managed in your device settings.\n\n"
                        + "4. How We Use Your Information\n"
                        + "The personal information we collect is used for the following purposes:\n"
                        + "• Account Creation and Management: Your email, password, or social media information is used to create and maintain your "
                        + "account. This allows you to log in, reset your password, and manage your profile within the Plynt app.\n"
                        + "• Content Personalization: Your personal information helps us tailor the content, including news articles, notifications, and "
                        + "recommendations, based on your interests, location, and preferences.\n"
                        + "• Service Improvements: We use usage data and interaction metrics to improve the functionality, design, and performance of Plynt. "
                        + "This includes analyzing how you interact with features such as article saving, comments, and voice-over.\n"
                        + "• Communication: We may use your email to send you service-related communications, such as notifications about important updates, "
                        + "feature announcements, or security alerts. We may also send marketing communications, but you will have the option to opt out "
                        + "of marketing emails.\n"
                        + "• Security and Fraud Detection: Your information is used to maintain the security of the Plynt platform and prevent unauthorized access, "
                        + "fraud, or malicious activity.\n\n"
                        + "5. Sharing and Disclosure of Information\n"
                        + "We take your privacy seriously, and Plynt will never sell, rent, or share your personal information with third-party advertisers or "
                        + "unrelated entities. However, in certain situations, we may share your data under the following conditions:\n"
                        + "• Service Providers: We may share your information with trusted service providers who assist in operating Plynt, such as data hosting "
                        + "providers, technical support, or analytics services. These providers are contractually obligated to maintain the confidentiality of "
                        + "your data and are prohibited from using it for any purpose other than providing their services to us.\n"
                        + "• Legal Obligations: We may disclose your personal information if required to do so by law, in response to a subpoena, court order, "
                        + "or other governmental requests. We will notify you of such requests unless prohibited by law.\n"
                        + "• Business Transfers: In the event of a merger, acquisition, or sale of Synec or Plynt, your personal information may be transferred as "
                        + "part of the business transaction. The acquiring party will be required to honor the commitments in this Policy.\n\n";

        String privacyPolicyContinuation = "6. Data Retention\n"
                + "We retain your personal information for as long as it is necessary to provide the services you have requested or as required "
                + "by applicable laws. If you choose to delete your account or stop using Plynt, you may delete your account and associated data "
                + "directly from the app’s settings. Once your account is deleted, all your personal data will be permanently and irreversibly removed "
                + "from our databases. This action is immediate and cannot be undone.\n\n"
                + "7. Your Rights and Control Over Your Data\n"
                + "You have full control over your personal data within Plynt. The following rights are provided to ensure your privacy and autonomy:\n"
                + "• Access and Correction: You may access and update your personal information at any time by visiting the settings section of the Plynt app. "
                + "If any information is inaccurate, you can modify it or request corrections.\n"
                + "• Data Deletion: You can delete your account and all associated data through the settings section of the Plynt app. Once initiated, "
                + "this process will permanently erase your data from all our databases. Please note that after deletion, your data will no longer be recoverable.\n"
                + "• Opting Out: You may opt-out of receiving marketing communications from Plynt by adjusting your preferences in the app’s settings or "
                + "by following the unsubscribe instructions in the emails you receive from us. However, you may continue to receive service-related messages "
                + "necessary for the operation of your account.\n"
                + "• Permissions Management: All permissions requested by the Plynt app can be managed via your device’s settings. You have the right to revoke "
                + "access to location, storage, microphone, and other permissions at any time. However, please note that certain features may not function properly "
                + "if permissions are denied.\n\n"
                + "8. Security Measures\n"
                + "We take the security of your personal information seriously and implement a range of measures to ensure that your data is protected from unauthorized "
                + "access, alteration, or disclosure. Our security practices include encryption of sensitive information, secure data storage, and regular security audits "
                + "to identify and mitigate potential vulnerabilities.\n\n"
                + "While we strive to use commercially acceptable means to protect your personal information, no method of transmission over the internet or method "
                + "of electronic storage is completely secure. Therefore, we cannot guarantee the absolute security of your data. You agree to use Plynt at your own risk.\n\n"
                + "9. Children’s Privacy\n"
                + "Plynt is not intended for use by individuals under the age of 18. We do not knowingly collect personal information from minors. If you are a parent or "
                + "guardian and believe that your child has provided personal data, please contact us immediately at hello@synecsoftware.com, and we will take steps to remove "
                + "that information from our records.\n\n"
                + "10. Third-Party Links and Services\n"
                + "Plynt may contain links to third-party websites, applications, or services. We are not responsible for the privacy practices or content of those third parties, "
                + "and this Policy does not apply to third-party services. We encourage you to review the privacy policies of any third-party services you engage with.\n\n"
                + "11. Limitation of Liability\n"
                + "To the maximum extent permitted by law, Synec and its affiliates, officers, employees, agents, or licensors shall not be liable for any indirect, incidental, or "
                + "consequential damages resulting from your use or inability to use Plynt, even if we have been advised of the possibility of such damages. This includes but is "
                + "not limited to any loss of data, security breaches, service interruptions, or unauthorized access to your personal information.\n\n"
                + "You agree that your use of Plynt is at your sole risk and that the platform is provided on an \"as-is\" and \"as-available\" basis without warranties of any kind, either "
                + "express or implied.\n\n"
                + "12. Changes to this Policy\n"
                + "We reserve the right to modify or update this Privacy Policy and Terms of Service at any time. When we make changes, we will update the \"Effective Date\" at the "
                + "top of this Policy and provide a notification either via email or within the app. It is your responsibility to periodically review the Policy to stay informed of any updates. "
                + "Continued use of Plynt following any changes to this Policy constitutes your acceptance of the revised terms.\n\n"
                + "13. Contact Us\n"
                + "If you have any questions, concerns, or requests related to this Privacy Policy or the use of your personal information, please contact us at:\n" +
                ""
                + "Synec Software Development Services\n"
                + "Email: hello@synecsoftware.com\n"
                + "Website: https://synecsoftware.com\n\n"
                + "By using Plynt, you acknowledge that you have read, understood, and agreed to the terms outlined in this Privacy Policy and Terms of Service.\n";

// Set the privacy policy text to the TextView
        privacyPolicyTextView.setText(privacyPolicy);
        privacyPolicyTextView.append(privacyPolicyContinuation);

    }
}