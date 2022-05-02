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

public class Movie  extends JPanel {
    JLabel fromTheaterLabel,fromMovieLabel,fromDateLabel,fromTimeTalbe;
    JComboBox fromTheater,fromMovie,fromDate,fromTimeTable;
    LinkedList<HashMap<String,Object>> results;
    LinkedList<Object>movie_list;
    String column_name,column_target;
    String tid, mid,theater_movie_id,ttid,session_id=null;
    Movie() {
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
        fromMovie = new JComboBox(get_movie_list().toArray());
        fromMovie.setBounds(150,80,150,25);
        add(fromMovieLabel);
        add(fromMovie);



        fromTheater.setSelectedIndex(0);
//        fromMovie.setSelectedIndex(0);
        fromTheater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromT = fromTheater.getSelectedItem().toString();
                System.out.println(fromT);
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
                    System.out.println(tid);

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }


            }
        });

        fromMovie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tid!=null){
                    String fromM=fromMovie.getSelectedItem().toString();
                    column_name="name";
                    column_target="mid";
                    String q2=String.format("select %s,%s from theater where theater_name = '%s'"
                            ,column_target,column_name,fromM);
                    try {
                        results=sql(q2);
                        System.out.println(results);
                        for(HashMap h: results){
                            mid=h.get(column_target).toString();
                        }
                        System.out.println(mid);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    JOptionPane.showMessageDialog(fromMovie,"請先輸入電影院");
                }
            }
        });
    }
    private LinkedList<Object>  get_movie_list(){
        try {
            String q2 = String.format("select movie.name from theater_movie_id as tmi " +
                    "join movie on tmi.mid=movie.mid where tid =%s", tid);
            results=sql(q2);
            for(HashMap h:results){
                movie_list.add(h.get("name"));
            }
            System.out.println(movie_list);
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
        return movie_list;
    }
    private LinkedList<HashMap<String,Object>> sql(String query) throws SQLException {

        LinkedList<HashMap<String,Object>> temp=new LinkedList<>();
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
