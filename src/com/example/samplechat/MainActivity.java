package com.example.samplechat;

import java.util.List;

import org.jivesoftware.smack.SmackException;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

public class MainActivity extends ActionBarActivity {

	private static final String APP_ID = "Your app id here";
	private static final String AUTH_KEY = "Your Auth key here";
	private static final String AUTH_SECRET = "Your Secret Key here";
	//
	private static final String USER_LOGIN = "sample1";
	private static final String USER_PASSWORD = "12345678";

	static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

	private QBChatService chatService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Init Chat
		//
		QBChatService.setDebugEnabled(true);
		QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
		if (!QBChatService.isInitialized()) {
			QBChatService.init(this);
		}
		chatService = QBChatService.getInstance();


		// create QB user
		//
		final QBUser user = new QBUser();
		user.setLogin(USER_LOGIN);
		user.setPassword(USER_PASSWORD);

		QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
			@Override
			public void onSuccess(QBSession session, Bundle args) {

				// save current user
				//
				user.setId(session.getUserId());
				((ApplicationSingleton) getApplication()).setCurrentUser(user);

				// login to Chat
				//
				loginToChat(user);
			}

			@Override
			public void onError(List<String> errors) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setMessage("create session errors: " + errors).create().show();
			}
		});
	}

	private void loginToChat(final QBUser user){

		chatService.login(user, new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {

				// Start sending presences
				//
				try {
					chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
				} catch (SmackException.NotLoggedInException e) {
					e.printStackTrace();
				}

				// go to Dialogs screen
				//
				Intent intent = new Intent(MainActivity.this, DialogsActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onError(List errors) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setMessage("chat login errors: " + errors).create().show();
			}
		});
	}
}
