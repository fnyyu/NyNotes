package com.cvter.nynote.View;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.R;

/**
 * Created by cvter on 2017/6/8.
 */

public class FilePopupWindow extends BasePopupWindow {

    private DrawActivity mContext;
    Button fileNameButton;
    EditText fileNameEditText;


    public FilePopupWindow(DrawActivity context, int width, int height) {
        super(context, width, height);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        View fileView = LayoutInflater.from(mContext).inflate(R.layout.window_file_name, null);
        this.setContentView(fileView);
        fileNameButton = (Button) fileView.findViewById(R.id.fileName_button);
        fileNameEditText = (EditText) fileView.findViewById(R.id.fileName_editText);
    }

    public void setListener() {
        fileNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mContext.finish();
            }
        });

    }




}
