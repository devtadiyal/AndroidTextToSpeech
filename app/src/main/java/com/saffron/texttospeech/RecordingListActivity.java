package com.saffron.texttospeech;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordingListActivity extends Activity {

    private RecyclerView recyclerView;
    String aa, name;
    List<String> l = new ArrayList<>();
    List<String> l2 = new ArrayList<>();
    MediaPlayer mediaPlayer;

    private List<Getter> list1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_list_layout);

        recyclerView = findViewById(R.id.rc);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well

                String title1 = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.t1)).getText().toString();
                mediaPlayer = new MediaPlayer();

                try {
                    //mediaPlayer.setDataSource("/storage/emulated/0/" + title1);
                    mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+title1);
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));



     /*   // For HardCore value
       */
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        File[] list = file.listFiles();
        int count = 0;
        for (File f : list) {
            name = f.getName();
            if (name.endsWith(".3gp"))
                count++;


            l.add(name);
            l2.add(f.getAbsolutePath());
            Iterator itr = l.iterator();
            while (itr.hasNext()) {
                String x = (String) itr.next();
                System.out.println("HELLO LENGTH " + x);
                if (!(x.contains(".3gp")))

                    itr.remove();
            }
        }


        for (int k = 0; k < l.size(); k++) {
            aa = l.get(k);
            System.out.println("SSS " + aa);
            Getter listItem = new Getter(aa, "");
            list1.add(listItem);
            System.out.println("ZZZZZZZ " + list1);

        }


        RecyclerView.Adapter adapter = new RecordingListAdapter(list1, this);
        recyclerView.setAdapter(adapter);

    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ItemClickListener clicklistener;
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context, final RecyclerView recycleView, final ItemClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}