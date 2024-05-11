package hpFeatures;

import java.io.*;
import java.util.*;

class HomeEaseRBTnode {
    String HomeEaseRBTinfnd;
    HomeEaseRBTnode HomeEaseRBT_lftnd;
    HomeEaseRBTnode HomeEaseRBTrghtnd;
    HomeEaseRBTnode HomeEaseRBTprntnd;
    String HomeEaseRBTclrval;
    int HomeEaseRBTincval;

    HomeEaseRBTnode(String word){
        this.HomeEaseRBTinfnd = word;
        this.HomeEaseRBT_lftnd = null;
        this.HomeEaseRBTrghtnd = null;
        this.HomeEaseRBTprntnd = null;
        this.HomeEaseRBTclrval = "Red";
        this.HomeEaseRBTincval = 0;
    }

    public String getHomeEaseRBTinfnd() {
        return HomeEaseRBTinfnd;
    }

    public void setHomeEaseRBTinfnd(String HomeEaseRBTinfnd) {
        this.HomeEaseRBTinfnd = HomeEaseRBTinfnd;
    }

    public HomeEaseRBTnode getHomeEaseRBT_lftnd() {
        return HomeEaseRBT_lftnd;
    }

    public void setHomeEaseRBT_lftnd(HomeEaseRBTnode HomeEaseRBT_lftnd) {
        this.HomeEaseRBT_lftnd = HomeEaseRBT_lftnd;
    }

    public HomeEaseRBTnode getHomeEaseRBTrghtnd() {
        return HomeEaseRBTrghtnd;
    }

    public void setHomeEaseRBTrghtnd(HomeEaseRBTnode HomeEaseRBTrghtnd
    ) {
        this.HomeEaseRBTrghtnd = HomeEaseRBTrghtnd;
    }

    public HomeEaseRBTnode getHomeEaseRBTprntnd() {
        return HomeEaseRBTprntnd;
    }

    public void setHomeEaseRBTprntnd(HomeEaseRBTnode HomeEaseRBTprntnd) {
        this.HomeEaseRBTprntnd = HomeEaseRBTprntnd;
    }

    public String getHomeEaseRBTclrval() {
        return HomeEaseRBTclrval;
    }

    public void setHomeEaseRBTclrval(String HomeEaseRBTclrval) {
        this.HomeEaseRBTclrval = HomeEaseRBTclrval;
    }
}

class HomeEaseRBT_Nil extends HomeEaseRBTnode {
    HomeEaseRBT_Nil(){
        super(null);
        this.setHomeEaseRBTclrval("Black");
    }
}

public class FrequencyCount {
    static HashMap<String, Integer> HomeEaseRBT_freq = new HashMap<String, Integer>();
    static HomeEaseRBTnode HomeEaseRBT_r = null;
    public static HashMap<String, Integer> HomeEaseRBT_sortByValue(HashMap<String, Integer> hm)
    {
        List<Map.Entry<String, Integer> > HomeEaseRBT_list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        Collections.sort(HomeEaseRBT_list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> HomeEaseRBT_o1,
                               Map.Entry<String, Integer> HomeEaseRBT_o2)
            {
                return (HomeEaseRBT_o2.getValue()).compareTo(HomeEaseRBT_o1.getValue());
            }
        });
        HashMap<String, Integer> HomeEaseRBT_temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> HomeEaseRBT_aa : HomeEaseRBT_list) {
            HomeEaseRBT_temp.put(HomeEaseRBT_aa.getKey(), HomeEaseRBT_aa.getValue());
        }
        return HomeEaseRBT_temp;
    }
    public static void HomeEaseRBTfxprntchld(HomeEaseRBTnode pt, HomeEaseRBTnode old_cd, HomeEaseRBTnode new_cd){
        if(pt == null){
            HomeEaseRBT_r = new_cd;
        }
        else if(pt.HomeEaseRBT_lftnd == old_cd){
            pt.HomeEaseRBT_lftnd = new_cd;
        }
        else if(pt.HomeEaseRBTrghtnd == old_cd){
            pt.HomeEaseRBTrghtnd = new_cd;
        }
        else{
            throw new IllegalStateException("ERROR\n");
        }
        if(new_cd != null){
            new_cd.HomeEaseRBTprntnd = pt;
        }
    }

    public static void HomeEaseRBTrghtrot(HomeEaseRBTnode rx){
        HomeEaseRBTnode pt = rx.HomeEaseRBTprntnd;
        HomeEaseRBTnode new_root = rx.HomeEaseRBT_lftnd;
        rx.HomeEaseRBT_lftnd = new_root.HomeEaseRBTrghtnd;
        if(new_root.HomeEaseRBTrghtnd != null){
            new_root.HomeEaseRBTrghtnd.HomeEaseRBTprntnd = rx;
        }
        new_root.HomeEaseRBTrghtnd = rx;
        rx.HomeEaseRBTprntnd = new_root;
        HomeEaseRBTfxprntchld(pt, rx, new_root);
    }

    public static void HomeEaseRBTlfrot(HomeEaseRBTnode rx){
        HomeEaseRBTnode pt = rx.HomeEaseRBTprntnd;
        HomeEaseRBTnode new_root = rx.HomeEaseRBTrghtnd;
        rx.HomeEaseRBTrghtnd = new_root.HomeEaseRBT_lftnd;
        if(new_root.HomeEaseRBT_lftnd != null){
            new_root.HomeEaseRBT_lftnd.HomeEaseRBTprntnd = rx;
        }
        new_root.HomeEaseRBT_lftnd = rx;
        rx.HomeEaseRBTprntnd = new_root;
        HomeEaseRBTfxprntchld(pt, rx, new_root);
    }

    public static void HomeEaseRBTfxaftrinsrtn(HomeEaseRBTnode rx){
        HomeEaseRBTnode pt = rx.HomeEaseRBTprntnd;
        if(pt == null) {
            rx.setHomeEaseRBTclrval("Black");
            return;
        }
        if(pt.HomeEaseRBTclrval.equals("Black")){
            return;
        }
        HomeEaseRBTnode grndpt = pt.HomeEaseRBTprntnd;
        if(grndpt == null){
            pt.setHomeEaseRBTclrval("Black");
            return;
        }
        HomeEaseRBTnode unclnde = null;
        if(grndpt.HomeEaseRBT_lftnd == pt){
            unclnde = grndpt.HomeEaseRBTrghtnd;
        }
        else if(grndpt.HomeEaseRBTrghtnd == pt){
            unclnde = grndpt.HomeEaseRBT_lftnd;
        }
        else{
            System.out.println("ERROR unclnde\n");
        }

        if(unclnde != null && unclnde.HomeEaseRBTclrval.equals("Red")){
            pt.setHomeEaseRBTclrval("Black");
            unclnde.setHomeEaseRBTclrval("Black");
            grndpt.setHomeEaseRBTclrval("Red");
            HomeEaseRBTfxaftrinsrtn(grndpt);
        }
        else if(pt == grndpt.HomeEaseRBT_lftnd){
            if(rx == pt.HomeEaseRBTrghtnd){
                HomeEaseRBTlfrot(pt);
                pt = rx;
            }
            HomeEaseRBTrghtrot(grndpt);
            pt.setHomeEaseRBTclrval("Black");
            grndpt.setHomeEaseRBTclrval("Red");
        }
        else{
            if(rx == pt.HomeEaseRBT_lftnd){
                HomeEaseRBTrghtrot(pt);
                pt = rx;
            }
            HomeEaseRBTlfrot(grndpt);
            pt.setHomeEaseRBTclrval("Black");
            grndpt.setHomeEaseRBTclrval("Red");
        }
    }

    public static boolean HomeEaseRBTinsrtwrd(String word){
        HomeEaseRBTnode rx = HomeEaseRBT_r;
        HomeEaseRBTnode pt = null;
        while(rx != null) {
            pt = rx;
            if(word.compareTo(rx.HomeEaseRBTinfnd) < 0){
                rx = rx.HomeEaseRBT_lftnd;
            }
            else if(word.compareTo(rx.HomeEaseRBTinfnd) > 0){
                rx = rx.HomeEaseRBTrghtnd;
            }
            else{
                return true;
            }
        }
        HomeEaseRBTnode nn = new HomeEaseRBTnode(word);
        nn.setHomeEaseRBTclrval("Red");
        nn.HomeEaseRBTincval = 1;
        HomeEaseRBT_freq.put(word, nn.HomeEaseRBTincval);
        if(pt == null){
            HomeEaseRBT_r = nn;
        }
        else if(word.compareTo(pt.HomeEaseRBTinfnd) < 0){
            pt.HomeEaseRBT_lftnd = nn;
        }
        else{
            pt.HomeEaseRBTrghtnd = nn;
        }
        nn.HomeEaseRBTprntnd = pt;
        HomeEaseRBTfxaftrinsrtn(nn);
        return false;
    }

    public static HomeEaseRBTnode HomeEaseRBTsrchwrd(String word){
        HomeEaseRBTnode rx = HomeEaseRBT_r;
        while(rx != null) {
            if(word.equals(rx.HomeEaseRBTinfnd)) {
                rx.HomeEaseRBTincval++;
                HomeEaseRBT_freq.put(word, rx.HomeEaseRBTincval);
                return rx;
            }
            else if(word.compareTo(HomeEaseRBT_r.HomeEaseRBTinfnd) < 0){
                rx = rx.HomeEaseRBT_lftnd;
            }
            else{
                rx = rx.HomeEaseRBTrghtnd;
            }
        }
        return null;
    }

    public static HomeEaseRBTnode HomeEaseRBTiordsucr(HomeEaseRBTnode rx){
        while(rx.HomeEaseRBT_lftnd != null){
            rx = rx.HomeEaseRBT_lftnd;
        }
        return rx;
    }

    public static HomeEaseRBTnode HomeEaseRBTsblng(HomeEaseRBTnode rx){
        HomeEaseRBTnode pt = rx.HomeEaseRBTprntnd;
        if(rx == pt.HomeEaseRBT_lftnd){
            return pt.HomeEaseRBTrghtnd;
        }
        else if(rx == pt.HomeEaseRBTrghtnd){
            return pt.HomeEaseRBT_lftnd;
        }
        else{
            throw new IllegalStateException("ERROR\n");
        }
    }

    public static void HomeEaseRBTiordsrch(HomeEaseRBTnode HomeEaseNd)
    {
        if (HomeEaseNd == null) {
            return;
        }
        HomeEaseRBTiordsrch(HomeEaseNd.HomeEaseRBT_lftnd);
        HomeEaseRBTiordsrch(HomeEaseNd.HomeEaseRBTrghtnd);
    }

    public static void HomeEaseRBT_parseReader(File f) throws IOException {
        Scanner sc = new Scanner(f);
        while(sc.hasNextLine()){
            String word = sc.nextLine();
            HomeEaseRBTnode found = HomeEaseRBTsrchwrd(word);
            if(found == null){
                HomeEaseRBTinsrtwrd(word);
            }
        }
        HashMap<String, Integer> HomeEaseRBT_sortedFreq = HomeEaseRBT_sortByValue(HomeEaseRBT_freq);
        File fout = new File("Text_Files/freq_output.txt");
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter(fout));
            for(Map.Entry<String, Integer> brrr: HomeEaseRBT_sortedFreq.entrySet()){
                bw.write(brrr.getKey());
                bw.newLine();
            }
            bw.flush();
        } catch(IOException e){
            //e.printStackTrace();
        }
        finally{
            try{
                bw.close();
            } catch(Exception e){}
        }
    }
}