package com.example.qrcode_scanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class QR_Code_Scan_Activity extends AppCompatActivity
{
    private CodeScanner codeScanner;

    CodeScannerView code_scanner_view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);

        code_scanner_view = (CodeScannerView) findViewById(R.id.code_scanner_view);

        codeScanner = new CodeScanner(this, code_scanner_view);

        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback()
        {
            @Override
            public void onDecoded(@NonNull Result result)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(QR_Code_Scan_Activity.this, result.getText(), Toast.LENGTH_LONG).show();

                        sendData(result.getText());

                        codeScanner.releaseResources();
                    }
                });
            }
        });

        code_scanner_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        codeScanner.releaseResources();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        codeScanner.startPreview();
    }

    public void sendData(String data)
    {
        BackgroundTask b1 = new BackgroundTask();
        b1.execute(data);
    }

    class BackgroundTask extends AsyncTask<String, Void, Void>
    {
        Socket socket;
        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids)
        {
            try
            {
                String data = voids[0];
                socket = new Socket("192.168.1.5", 9992);
                writer = new PrintWriter(socket.getOutputStream());

                writer.write(data);
                writer.flush();

                writer.close();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }

            return null;
        }
    }
}