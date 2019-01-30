package com.example.project.myapplication.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.myapplication.R;

/**
 * Created by Andia on 13/12/2017.
 */

public class JavaDialog extends AppCompatDialogFragment {
    private EditText etFrequency;
    private JavaDialog.JavaDialogListener listener;
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_java, null);


        builder.setView(view)
                .setTitle("Enter Frequency in seconds")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                        // String broker = etBroker.getText().toString();
                        //String port = etPort.getText().toString();
                        //listener.applyText(broker,port);
                    }
                })
                .setCancelable(false);

        etFrequency = (EditText) view.findViewById(R.id.frequencyText);
        return builder.create();
    }
    //onStart() is where com.example.project.myapplication.dialog.show() is actually called on
//the underlying com.example.project.myapplication.dialog, so we have to do it there or
//later in the lifecycle.
//Doing it in onResume() makes sure that even if there is a config change
//environment that skips onStart then the com.example.project.myapplication.dialog will still be functioning
//properly after a rotation.
    @Override
    public void onResume()
    {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    String frequency = etFrequency.getText().toString();
                    if(!frequency.isEmpty()){
                        wantToCloseDialog = true;
                        listener.applyFrequency(frequency);
                    }else{
                        Toast.makeText(getContext(),"Fill empty fields", Toast.LENGTH_SHORT).show();
                    }

                    if(wantToCloseDialog){
                        d.dismiss();
                    }
                    //else com.example.project.myapplication.dialog stays open. Make sure you have an obvious way to close the com.example.project.myapplication.dialog especially if you set cancellable to false.

                }
            });
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (JavaDialog.JavaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener here too");
        }
    }

    public interface JavaDialogListener{
        void applyFrequency(String frequency);
    }
}

