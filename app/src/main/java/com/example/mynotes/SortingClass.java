package com.example.mynotes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortingClass {
    public void sort(ArrayList<Notes> list)
    {
        Collections.sort(list, new Comparator<Notes>() {
            @Override
            public int compare(Notes o1, Notes o2) {
                String d1=o1.getDate();
                String t1=o1.getTime();
                String d2=o2.getDate();
                String t2=o2.getTime();
                int y1=Integer.parseInt(d1.substring(6));
                int m1=Integer.parseInt(d1.substring(3,5));
                int day1=Integer.parseInt(d1.substring(0,2));
                int h1=Integer.parseInt(t1.substring(0,2));
                int min1=Integer.parseInt(t1.substring(3,5));
                int sec1=Integer.parseInt(t1.substring(6));
                ArrayList<Integer> l1=new ArrayList<>();
                l1.add(y1);l1.add(m1);l1.add(day1);l1.add(h1);l1.add(min1);l1.add(sec1);

                int y2=Integer.parseInt(d2.substring(6));
                int m2=Integer.parseInt(d2.substring(3,5));
                int day2=Integer.parseInt(d2.substring(0,2));
                int h2=Integer.parseInt(t2.substring(0,2));
                int min2=Integer.parseInt(t2.substring(3,5));
                int sec2=Integer.parseInt(t2.substring(6));
                ArrayList<Integer> l2=new ArrayList<>();
                l2.add(y2);l2.add(m2);l2.add(day2);l2.add(h2);l2.add(min2);l2.add(sec2);

                int v=0;
                for(int i=0;i<l1.size();i++)
                {
                    if(l1.get(i)>l2.get(i))
                    {
                        v=-1;
                        break;
                    }
                    else if(l1.get(i)<l2.get(i))
                    {
                        v=1;
                        break;
                    }
                }
                return v;
            }
        });
    }
}
