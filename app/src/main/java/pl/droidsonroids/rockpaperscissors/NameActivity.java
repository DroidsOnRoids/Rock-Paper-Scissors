package pl.droidsonroids.rockpaperscissors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        ButterKnife.bind(this);

        UserPreferences userPreferences = UserPreferences.getInstance(this);
        if (!TextUtils.isEmpty(userPreferences.getUserName())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @OnEditorAction(R.id.edit_name)
    void onNameInput(EditText editText, int keyCode, KeyEvent keyEvent) {
        String name = editText.getEditableText().toString();
        if (keyCode == KeyEvent.KEYCODE_ENTER && !TextUtils.isEmpty(name)) {
            UserPreferences.getInstance(this).setUserName(name);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
