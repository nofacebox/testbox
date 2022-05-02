package com.kuang.note;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

public class MovieV2  extends JPanel {
    JLabel fromTheaterLabel,fromMovieLabel,fromDateLabel,fromTimetableLabel;
    JComboBox fromTheater,fromMovie,fromDate,fromTimetable;
    LinkedList<HashMap<String,Object>> results;
    LinkedList<Object>movie_list=new LinkedList<>();
    LinkedList<Object>date_list=new LinkedList<>();
    LinkedList<Object>session_time_list=new LinkedList<>();
    String column_name,column_target;
    String tid, mid,theater_movie_id,ttid,session_id=null;
    MovieV2() {
        new JPanel();
        setLayout(null);
        setBounds(0,0,480,640);


        fromTheaterLabel = new JLabel("請選擇電影院");
        fromTheaterLabel.setBounds(30,30,100,30);
        fromTheater = new JComboBox(new String[]{"台北松仁威秀影城",
                "台北信義威秀影城", "新竹大遠百威秀影城", "台中Tiger City威秀影城",
                "台南南紡威秀影城",});
        fromTheater.setBounds(150,30,150, 25);
        add(fromTheaterLabel);
        add(fromTheater);


        fromMovieLabel = new JLabel("請選擇電影");
        fromMovieLabel.setBounds(30,80,100,30);
         String [] initM=new String[]{"please insert theater"};
        fromMovie = new JComboBox(initM);
        fromMovie.setBounds(150,80,150,25);
        add(fromMovieLabel);
        add(fromMovie);

        fromDateLabel = new JLabel("請選擇日期");
        fromDateLabel.setBounds(30,130,100,30);
        String [] initD=new String[]{"please insert theater first"};
        fromDate = new JComboBox(initD);
        fromDate.setBounds(150,130,150,25);
        add(fromDateLabel);
        add(fromDate);

        fromTimetableLabel = new JLabel("請選擇場次");
        fromTimetableLabel.setBounds(30,180,100,30);
        String [] initTT=new String[]{"please insert timetable first"};
        fromTimetable = new JComboBox(initTT);
        fromTimetable.setBounds(150,180,150,25);
        add(fromTimetableLabel);
        add(fromTimetable);


        fromTheater.setSelectedIndex(0);
        fromMovie.setSelectedIndex(0);
        fromDate.setSelectedIndex(0);
        fromTimetable.setSelectedIndex(0);
        fromTheater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e1) {
                String fromT = fromTheater.getSelectedItem().toString();
                System.out.println(fromT);
                //把選到的電影院字串轉成tid
                column_name = "theater_name";
                column_target = "tid";
                String q1 = String.format("select %s,%s from theater where theater_name = '%s'"
                        , column_target, column_name, fromT);
                try {
                    results = sql(q1);
                    System.out.println(results);
                    for (HashMap h : results) {
                        tid = h.get(column_target).toString();
                    }
                    System.out.println("tid="+tid);

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
                //取得電影院選取的getitem後, 再執行sql查詢更新from_movie
                get_movie_list();

            }

        });
        fromMovie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e2) {
                //把選到的電影字串轉成mid
                String fromM = fromMovie.getSelectedItem().toString();
                System.out.println(fromM);
                //把選到的電影字串轉成mid
                column_name = "name";
                column_target = "mid";
                String q3 = String.format("select %s,%s from movie where name = '%s'"
                        , column_target, column_name, fromM);
                try {
                    results = sql(q3);
                    System.out.println(results);
                    for (HashMap h : results) {
                        mid = h.get(column_target).toString();
                    }
                    System.out.println(mid);

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
                //取得電影院選取的getitem後, 再執行sql查詢 更新from_date
                get_date_list();
            }

        });


         fromDate.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e2) {
                 String fromD = fromDate.getSelectedItem().toString();

                 get_timetable_list(fromD);
             }

         });





    }
    private void  get_movie_list(){
        try {
            movie_list.clear();
            try {
                fromMovie.removeAllItems();
            }catch (Exception e){};
            column_name="tid";
            column_target="name";
            String q2 = String.format("select movie.name,tid from mid_theater_movie as mtm " +
                    "join movie on mtm.mid=movie.mid where tid = '%s'", tid);
            results=sql(q2);
            System.out.println(results);
            for(HashMap h:results){
                movie_list.add(h.get("name"));
            }
            fromMovie.setModel(new DefaultComboBoxModel(movie_list.toArray()));
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

    }
    private LinkedList<Object> get_timetable_list(String date){
        //有了中間表的id, 日期, 可以使用join 拿取得
        date_list.clear();
        try {
            System.out.println(mid);
            column_name="ttid"; //隨便填一個欄位 只是要符合sql()裡的column_name有一個欄位
            column_target="session_time";
            String q6 = String.format("select session_time,mms.ttid,session_time from mid_movie_session as mms " +
                    "join timetable as t on mms.ttid= t.ttid where theater_movie_id=" +
                    "%s and session_day='%s'",theater_movie_id,date);
            results = sql(q6);
            System.out.println(results);
            for (HashMap h : results) {
                session_time_list.add(h.get(column_target));
            }
            System.out.println("theater_movie_id="+theater_movie_id);
            fromTimetable.setModel(new DefaultComboBoxModel(session_time_list.toArray()));



        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return date_list;
    }
    private LinkedList<Object> get_date_list(){
        //有了tid, mid, 可以拿到中間表的id
            date_list.clear();
        try {
            System.out.println(mid);
            column_name="tid"; //隨便填一個欄位 只是要符合sql()裡的column_name有一個欄位
            column_target="theater_movie_id";
            String q4 = String.format("select theater_movie_id,tid from mid_theater_movie " +
                    "where tid='%s' and mid='%s'",tid,mid);
            results = sql(q4);
            System.out.println(results);
            for (HashMap h : results) {
                theater_movie_id= h.get(column_target).toString();
            }
            System.out.println("theater_movie_id="+theater_movie_id);


            column_name="theater_movie_id";
            column_target="session_day";
            String q5 = String.format("select distinct session_day,theater_movie_id from mid_movie_session as mms " +
                    "where theater_movie_id='%s'",theater_movie_id);
            results=sql(q5);
            System.out.println(results);
            for(HashMap h:results){
                date_list.add(h.get("session_day"));
            }
            fromDate.setModel(new DefaultComboBoxModel(date_list.toArray()));
            System.out.println(date_list);
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return date_list;
    }
    private LinkedList<HashMap<String,Object>> sql(String query) throws SQLException {

        LinkedList<HashMap<String,Object>> temp=new LinkedList<>();
        //1. load driver(新版的java可以省略)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
        try{
            Properties prop = new Properties();
            prop.put("user", "root");
            prop.put("password", "");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/movie", prop);


            PreparedStatement pstmt= conn.prepareStatement(
                    query);

            ResultSet rs= pstmt.executeQuery();
            while (rs.next()) {
                HashMap<String,Object> data=new HashMap<>();
                data.put(column_target,rs.getString(column_target));
                data.put(column_name,rs.getString(column_name));
                temp.add(data);
                System.out.println("OK");
            }
            conn.close();
        }catch (Exception e){
            System.out.println(e.toString());
        }


        return temp ;
    }
}
