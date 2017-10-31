package com.example.mandeepkaur.breakoutgame.utilities;

import android.content.Context;
import android.os.Environment;

import com.example.mandeepkaur.breakoutgame.models.ScoreDataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileHandler {

    Context fileContext;

    public FileHandler(Context fileContext){
        this.fileContext=fileContext;
    }

    public void setDataObject(ArrayList<ScoreDataModel> inf){
        clearContents();
        try {

            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" );
            File foo=new File(dir,"temp11.txt");

            PrintStream pr = new PrintStream(fileContext.openFileOutput(foo.getName(), Context.MODE_PRIVATE));

            for(ScoreDataModel temp:inf){
                String str;

                str=temp.getName();
                str="(name:"+str+")";
                pr.print(str);

                str=temp.getScore();
                str="(score:"+str+")";
                pr.print(str);

                str=temp.getTime();
                str="(time:"+str+")";
                pr.print(str);

                str=temp.getId();
                str="(id:"+str+")";
                pr.print(str);

                pr.println("");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<ScoreDataModel> getDataObject(){
        ArrayList<ScoreDataModel> inf=new ArrayList<ScoreDataModel>();
        String str;

        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download" );
        File foo=new File(dir,"temp11.txt");

        if(!foo.exists()) {
            try {
                foo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            FileInputStream fis=fileContext.openFileInput(foo.getName());
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            while((str=br.readLine())!=null)
            {
                inf.add(convertStringtoObject(str));
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        Collections.sort(inf);
        return inf;
    }

    private ScoreDataModel convertStringtoObject(String s){
        HashMap<String,String> temp=convertStringtoMap(s);
        ScoreDataModel inf=new ScoreDataModel();
        for(Map.Entry<String, String> e : temp.entrySet()){


            if(e.getKey().equals("name")){
                inf.setName(e.getValue());
            }

            if(e.getKey().equals("score")){
                inf.setScore(e.getValue());
            }

            if(e.getKey().equals("time")){
                inf.setTime(e.getValue());
            }

            if(e.getKey().equals("id")){
                inf.setId(e.getValue());
            }

        }
        return inf;

    }

    private HashMap<String,String> convertStringtoMap(String s){
        HashMap<String,String> temp=new HashMap<String,String>();
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(s);
        while(m.find()) {
            String [] str=m.group(1).split(":",2);
            temp.put(str[0],str[1]);
        }
        return temp;
    }

    private void clearContents(){
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" );
        File foo=new File(dir,"temp11.txt");
        try{
            FileOutputStream writer = fileContext.openFileOutput(foo.getName(),Context.MODE_PRIVATE);

            writer.write(("").getBytes());

            writer.close();
        }catch(FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  String getMaxId() {
        ScoreDataModel temp;
        String str;
        int maxid=0;
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download" );
        File foo=new File(dir,"temp11.txt");
        try{
            FileInputStream fis=fileContext.openFileInput(foo.getName());
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            while((str=br.readLine())!=null)
            {
                temp=convertStringtoObject(str);
                if(Integer.parseInt(temp.getId())>maxid){
                    maxid=Integer.parseInt(temp.getId());
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return String.valueOf(maxid);
    }

    public int isInTopTen(ScoreDataModel obj){
        ArrayList<ScoreDataModel> arr,finalArr;
        arr=getDataObject();
        if(arr.size() < 10){
            return 1;
        }
        int compareScore,compareTime;
        for(ScoreDataModel temp : arr){
            compareScore=obj.getScore().compareTo(temp.getScore());
            compareTime=obj.getTime().compareTo(temp.getTime());
            if(compareScore==1||(compareScore==0 && compareTime==1)){
                return 1;
            }
        }
        return 0;
    }

}