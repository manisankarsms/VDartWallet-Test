package com.techbros.mycoinsTest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Feedback extends AppCompatActivity {

    ArrayList<FeedbackModel> feedbackArrayList = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mycoins-811bc-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference gRefH = database.getReference("feedbacks");
    Button downFeed;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        downFeed = findViewById(R.id.materialButton4);
        loadData();

        downFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportIntoExcel(feedbackArrayList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadData() {
        gRefH.orderByKey().addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String id  = dataSnapshot.child(key).child("userId").getValue().toString();
                    String name  = dataSnapshot.child(key).child("userName").getValue().toString();
                    String comment  = dataSnapshot.child(key).child("feedback").getValue().toString();
                    String date = dataSnapshot.child(key).child("datetime").getValue().toString();
                    String fId = dataSnapshot.child(key).child("fId").getValue().toString();
                    feedbackArrayList.add(new FeedbackModel(comment,id,name,date,fId));
                }
                Collections.reverse(feedbackArrayList);
                setListView(feedbackArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void setListView(ArrayList<FeedbackModel> feedbackArrayList) {
        ListView listView = findViewById(R.id.fLV);
        FeedbackAdapter adapter = new FeedbackAdapter(this, feedbackArrayList);
        listView.setAdapter(adapter);
    }

    private void exportIntoExcel(ArrayList<FeedbackModel> feedbackList) throws IOException {
        // Blank workbook
        HSSFWorkbook workbook = new HSSFWorkbook();

        // Creating a blank Excel sheet
        HSSFSheet sheet = workbook.createSheet("Report");
        int rowCount = 1;
        Row row;
        row = sheet.createRow(0);
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("USER ID");
        row.createCell(1).setCellValue("USER NAME");
        row.createCell(2).setCellValue("DATE TIME");
        row.createCell(3).setCellValue("FEEDBACK");


        for (FeedbackModel t : feedbackList) {
            row = sheet.createRow(rowCount++);
            writeBook(t, row);
        }
        sheet.setDefaultColumnWidth(20);

        String fileName = Transaction.generateTId();
        verifyStoragePermissions(Feedback.this);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Feedback"+fileName+".xls");
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Uri uri = Uri.parse(String.valueOf(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS)));
            new MaterialAlertDialogBuilder(this)
                    .setTitle("DOWNLOAD")
                    .setMessage("Report Downloaded Successfully!")
                    .setCancelable(false)
                    .setPositiveButton("OPEN LOCATION",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openDirectory(uri);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();
            Log.e(TAG, "Writing file" + file);
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void writeBook(FeedbackModel t, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(t.id);
        row.createCell(1).setCellValue(t.name);
        row.createCell(2).setCellValue(t.date);
        row.createCell(3).setCellValue(t.feedback);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We dont have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(uriToLoad,  "application/vnd.ms-excel");
        startActivity(intent);
    }
}
class FeedbackModel{
  String feedback,id,name,date,fid;

    public FeedbackModel(String feedback, String id, String name, String date, String fId) {
        this.feedback = feedback;
        this.id = id;
        this.name = name;
        this.date = date;
        this.fid = fId;

    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFid() { return fid; }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

class FeedbackAdapter extends ArrayAdapter<FeedbackModel> implements Filterable {


    public FeedbackAdapter(Activity context, ArrayList<FeedbackModel> feedbackDetails){
        super(context, 0, feedbackDetails);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.feedback_list, parent, false
            );
        }

        FeedbackModel feedback = getItem(position);

        TextView textView1 = (TextView) listItemView.findViewById(R.id.feedback);
        textView1.setText(feedback.getFeedback());

        TextView textView2 = (TextView) listItemView.findViewById(R.id.name);
        textView2.setText(feedback.getName());

        TextView textView3 = (TextView) listItemView.findViewById(R.id.id);
        textView3.setText("("+feedback.getId()+")");

        TextView textView4 = (TextView) listItemView.findViewById(R.id.date);
        textView4.setText(feedback.getDate());

        return listItemView;
    }
}
