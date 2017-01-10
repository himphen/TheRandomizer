package hibernate.v2.therandomizer.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import hibernate.v2.therandomizer.R;

public class MainFragment extends BaseFragment {

	@BindView(R.id.inputText1)
	EditText inputText1;
	@BindView(R.id.inputText2)
	EditText inputText2;
	@BindView(R.id.inputText3)
	EditText inputText3;
	@BindView(R.id.buttonAdd)
	Button buttonAdd;
	@BindView(R.id.buttonRecord)
	Button buttonRecord;
	@BindView(R.id.buttonSubmit)
	Button buttonSubmit;
	@BindView(R.id.resultText)
	TextView resultText;
	@BindView(R.id.resultHintText)
	TextView resultHintText;


	private ArrayList<String> outputList = new ArrayList<>();
	private String[] demoStringArray;

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.bind(this, rootView);
		mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		demoStringArray = getResources().getStringArray(R.array.demo);

		buttonAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
						.title(R.string.add_item_title)
						.items(R.array.catalog)
						.itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
							@Override
							public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
								chooseLine(demoStringArray[which]);
								return true;
							}
						})
						.neutralText(R.string.clear_netbtn)
						.onNeutral(new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
								chooseClearLine();
							}
						});
				dialog.show();
			}
		});

		buttonSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				String text1 = randomString(inputText1.getText().toString());
				String text2 = randomString(inputText2.getText().toString());
				String text3 = randomString(inputText3.getText().toString());
				String result = text1 + text2 + text3;
				copyString(result, false);
				outputList.add(0, result);
				resultText.setText(result);
				resultHintText.setVisibility(View.VISIBLE);
			}
		});

		buttonRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
						.title(R.string.record_title)
						.items(outputList)
						.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
							@Override
							public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
								copyString(text, true);
								return false;
							}
						})
						.negativeText(R.string.ui_cancel)
						.neutralText(R.string.record_netbtn)
						.onNeutral(new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
								outputList.clear();
								Toast.makeText(mContext, R.string.record_clear, Toast.LENGTH_SHORT).show();
							}
						});
				dialog.show();
			}
		});
	}

	private void chooseClearLine() {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.clear_line_title)
				.items(R.array.line)
				.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						switch (which) {
							case 0:
								inputText1.setText("");
								break;
							case 1:
								inputText2.setText("");
								break;
							case 2:
								inputText3.setText("");
								break;
						}
						return false;
					}
				})
				.negativeText(R.string.ui_cancel);
		dialog.show();
	}

	private void chooseLine(final String aString) {
		MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
				.title(R.string.add_line_title)
				.items(R.array.line)
				.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						TextView textView;
						switch (which) {
							case 0:
								textView = inputText1;
								break;
							case 1:
								textView = inputText2;
								break;
							case 2:
								textView = inputText3;
								break;
							default:
								return false;
						}
						String temp = textView.getText().toString();
						if (TextUtils.isEmpty(temp) || temp.substring(temp.length() - 1).equals(",")) {
							textView.append(aString);
						} else {
							textView.append("," + aString);
						}
						return false;
					}
				})
				.negativeText(R.string.ui_cancel);
		dialog.show();

	}

	private String randomString(String itemString) {
		itemString = itemString.replaceAll("，", ","); // replace ","

		String[] itemArray = itemString.split(",");
		int resultIndex = new Random().nextInt(itemArray.length);
		return itemArray[resultIndex];
	}

	private void copyString(CharSequence text, boolean isShowToast) {
		ClipboardManager clipboard = (android.content.ClipboardManager)
				mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = android.content.ClipData.newPlainText("text label", text);
		clipboard.setPrimaryClip(clip);

		if (isShowToast) {
			Toast.makeText(mContext, R.string.record_confirm, Toast.LENGTH_SHORT).show();
		}
	}
}
