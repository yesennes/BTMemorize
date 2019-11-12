package com.lsenseney.btmemorize.model;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Mar 27, 19
 **/
public class Memorize {
    public static void main(String[] args) throws IOException{
//        Scanner scan = new Scanner(System.in);
//        BufferedReader file = new BufferedReader(new FileReader(args[0]));
//        if(args.length == 2){
//            if(args[1].equals("-t")){
//                Node tree = buildTree(file.lines());
//                quiz(tree.toString(), scan, .04);
//            }else
//                treeApproach(file.lines(), Integer.valueOf(args[1]), .04);
//        }else
//            treeApproach(file.lines(), .04);
    }

    public static void treeApproach(Stream<String> verses, int startVerse, double allowedDiff){
        Scanner scan = new Scanner(System.in);
        //Replaces direction quote and long dash with typeable keys
        Node tree = buildTree(verses);

        System.out.println("Starting with verse " + (startVerse));
        memorize(tree, scan, startVerse, allowedDiff, null);
    } 

    public static void treeApproach(Stream<String> verses, double allowedDiff){
        Scanner scan = new Scanner(System.in);
        //Replaces direction quote and long dash with typeable keys
        String[] nodes = removeNonTypeable(verses);
        Node tree = buildTree(nodes, 0, nodes.length);

        int max = -1;
        System.out.println("Type as much as you know");
        String attempt = scan.nextLine().trim();
        String recitedTo = "";
        for(int j = 0; j < nodes.length; j++){
            recitedTo += nodes[j];
            EditDistance dist = EditDistance.editDistance(attempt.substring(0,
                        Math.min(attempt.length(), recitedTo.length())), recitedTo);
            if(dist.changes > j + 1 || dist.changes < 0){
                System.out.println("Failed at verse " + (j + 1));
                j++;
                while(j < nodes.length && recitedTo.length() < attempt.length()){
                    if(recitedTo.endsWith("-"))
                        recitedTo += nodes[j];
                    else
                        recitedTo +=  " " + nodes[j];
                    j++;
                }
                j = nodes.length;
            }else if(j > max)
                max = j;
            if(!recitedTo.endsWith("-"))
                recitedTo += " ";
        }
        EditDistance dist = EditDistance.editDistance(attempt, recitedTo);
        System.out.println(dist.marked);
        System.out.println("\nPress enter to continue");
        scan.nextLine();
        for(int k = 0; k < 40; k++){
            System.out.println();
        }
        
        System.out.println("Starting with verse " + (max + 2));
        memorize(tree, scan, max + 2, allowedDiff, null);
    }

    public static void memorize(Node toMemorize, Scanner scan, int startWith, double allowedDiff, String previousHint){
        if(toMemorize.endNumber >= startWith){
            if(toMemorize.previousChild != null){
                if(toMemorize.previousChild.endNumber >= startWith)
                    memorize(toMemorize.previousChild, scan, startWith, allowedDiff, previousHint);
                if(toMemorize.afterChild.endNumber >= startWith){
                    String previous = toMemorize.previousChild.toString();
                    String hint = "";
                    for(int i = 0; i < 3; i++){
                        int lastIndex = previous.lastIndexOf(" ");
                        hint = previous.substring(lastIndex) + hint;
                        previous = previous.substring(0, lastIndex);
                    }
                    memorize(toMemorize.afterChild, scan, startWith, allowedDiff, hint);
                }
            }
            boolean correct = false;
            do{
                if(previousHint != null)
                    System.out.println(previousHint + "...");
                System.out.printf("Type verses %d-%d%n", toMemorize.startNumber, toMemorize.endNumber);
                correct = quiz(toMemorize.toString(), scan, allowedDiff);
                for(int i = 0; i < 40; i++){
                    System.out.println();
                }
            }while(!correct);
        }
    }

    public static boolean quiz(String toMemorize, Scanner scan, double allowedDiff){
        String userIn = scan.nextLine().trim();
        EditDistance result = EditDistance.editDistance(userIn, toMemorize);
        int mistakesAllowed = (int)(toMemorize.length() * allowedDiff);
        if(result.changes == 0){
            System.out.println("Correct!");
            return true;
        }else if(result.changes <= mistakesAllowed && result.changes > 0) {
            System.out.println("Close. Yours vs correct:");
            System.out.println(result.marked);
            System.out.println("Accept? (y/n)");
            return scan.nextLine().equalsIgnoreCase("y");
        }else {
            System.out.println("Incorrect!");
            System.out.println("Yours:");
            System.out.println(userIn);
            System.out.println("Correct:");
            System.out.println(toMemorize);
            System.out.println("Differences:");
            System.out.println(result.marked);
            System.out.println("Press enter to continue");
            scan.nextLine();
            return false;
        }
    }

    private static Node buildTree(Stream<String> lines){
        String[] nodes = removeNonTypeable(lines);
        return buildTree(nodes, 0, nodes.length);
    }

    private static Node buildTree(String[] nodes, int start, int end){
        if(start + 1 == end){
            return new Node(nodes[start], start + 1);
        }
        return new Node(buildTree(nodes, start, start + (end - start + 1) / 2), buildTree(nodes, start + (end - start + 1) / 2, end));
    }

    private static class Node {
        public Node(String s, int number){
            chunk = s;
            startNumber = number;
            endNumber = number;
        }

        public Node(Node previousChild, Node afterChild){
            this.previousChild = previousChild;
            this.afterChild = afterChild;
            this.startNumber = previousChild.startNumber;
            this.endNumber = afterChild.endNumber;
        }

        String chunk;
        int startNumber;
        int endNumber;
        Node previousChild;
        Node afterChild;

        public String toString(){
            if(chunk != null)
                return chunk;
            return previousChild.toString().endsWith("-") ? previousChild.toString() + afterChild : previousChild + " " + afterChild;
        }

        boolean isLeaf(){
            return chunk == null;
        }
    }

    public static String[] removeNonTypeable(Stream<String> lines){
        String[] nodes = lines.map(s -> s.trim().replaceAll("[\u201c\u201d]", "\"").replace('\u2014', '-')
                .replace('\u2019', '\'')).toArray(i -> new String[i]);
        return nodes;
    }

    public static void livesApproach(String text, int peekWidth, int charactersPerLives){
        Scanner scan = new Scanner(System.in);
        String restart;
        do{
            int startingIndex = 0;
            int lives = 0;
            while(lives >= 0){
                System.out.println("Lives: " + lives + ". Enter text:");
                System.out.print(text.substring(Math.max(0, startingIndex - peekWidth), startingIndex));
                String userIn = scan.nextLine().trim();
                int endIndex = startingIndex + userIn.length();
                if(endIndex <= text.length() && userIn.equals(text.trim().substring(startingIndex, endIndex))){
                    System.out.println("Correct!");
                    int previousStart = startingIndex;
                    startingIndex += userIn.length();
                    lives += startingIndex / charactersPerLives - previousStart / charactersPerLives;
                }else{
                    System.out.println("Incorrect! Yours vs correct:");
                    System.out.println(userIn);
                    System.out.println(text.trim().substring(startingIndex, endIndex));
                    System.out.println(text.substring(startingIndex, Math.min(text.length(), startingIndex + peekWidth)).trim());
                    System.out.println("Press enter to continue");
                    scan.nextLine();
                    lives--;
                    for(int i = 0; i < 40; i++){
                        System.out.println();
                    }
                }
            }
            System.out.println("Game over, memorized " + startingIndex + " characters");
            System.out.println("restart? (y/n)");
            restart = scan.nextLine();
        }while(restart.equals("y"));
    }

}
