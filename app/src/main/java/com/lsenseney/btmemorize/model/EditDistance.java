package com.lsenseney.btmemorize.model;

import java.util.LinkedList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Deque;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Mar 27, 19
 **/
public class EditDistance {
    public final int changes;
    public final String marked;

    private EditDistance(int changes, String marked){
        this.changes = changes;
        this.marked = marked;
    }


    private static class CalculationRequest implements Comparable<CalculationRequest>{
        int startIndex;
        int destIndex;
        int changesToHere;
        CalculationRequest requestor;

        CalculationRequest(int start, int destIndex, int changesToHere, CalculationRequest requestor){
            this.startIndex = start;
            this.destIndex = destIndex;
            this.changesToHere = changesToHere;
            this.requestor = requestor;
        }

        /**
         * Note some objects that aren't equal may return 0 here
         */
        public int compareTo(CalculationRequest calc){
            int changeDiff = changesToHere - calc.changesToHere;
            if(changeDiff != 0)
                return changeDiff;
            int distanceIntoString = (calc.startIndex + calc.destIndex) - (startIndex + destIndex);
            if(distanceIntoString != 0){
                return distanceIntoString;
            }
            return (requestor != null ? requestor.startIndex : 0)  - (calc.requestor != null ? calc.requestor.startIndex : 0);
        }

        public boolean equals(Object o){
            if(!(o instanceof CalculationRequest))
                return false;
            CalculationRequest c = (CalculationRequest)o;
            return startIndex == c.startIndex && destIndex == c.destIndex && changesToHere == c.changesToHere;
        }

        public int hashCode(){
            return startIndex + destIndex + changesToHere;
        }
    }

    public static EditDistance editDistance(String start, String dest){
        final Map<Integer, Set<Integer>> workspace = new HashMap<>();
        PriorityQueue<CalculationRequest> requests = new PriorityQueue<CalculationRequest>();
        CalculationRequest bestFinished = null;
        CalculationRequest cur = new CalculationRequest(0, 0, 0, null);
        while(cur != null && (bestFinished == null || cur.changesToHere < bestFinished.changesToHere)){
            int startIndex = cur.startIndex;
            int destIndex = cur.destIndex;
            Set<Integer> atStart = workspace.get(startIndex);
            if(atStart != null && atStart.contains(destIndex)){
                workspace.get(startIndex).add(destIndex);
                if(startIndex == start.length() && destIndex == dest.length()){
                    if(bestFinished == null || bestFinished.changesToHere > cur.changesToHere)
                        bestFinished = cur;
                } else if(startIndex == start.length()){
                    CalculationRequest c = new CalculationRequest(start.length(), dest.length(),
                            cur.changesToHere + dest.length() - destIndex, cur);
                    if(bestFinished == null || bestFinished.changesToHere > c.changesToHere)
                        bestFinished = c;
                } else if(destIndex == dest.length()){
                    CalculationRequest c = new CalculationRequest(start.length(), dest.length(),
                            cur.changesToHere + start.length() - startIndex, cur);
                    if(bestFinished == null || bestFinished.changesToHere > c.changesToHere)
                        bestFinished = c;
                } else if(start.charAt(startIndex) == dest.charAt(destIndex)){
                    while(startIndex < start.length() && destIndex < dest.length() && start.charAt(startIndex) == dest.charAt(destIndex)){
                        startIndex++;
                        destIndex++;
                    }
                    requests.add(new CalculationRequest(startIndex, destIndex, cur.changesToHere, cur));
                }else{
                    requests.add(new CalculationRequest(startIndex + 1, destIndex, cur.changesToHere + 1, cur));
                    requests.add(new CalculationRequest(startIndex, destIndex + 1, cur.changesToHere + 1, cur));
                }
            }
            if(requests.isEmpty())
                cur = null;
            else
                cur = requests.remove();
        }
        cur = bestFinished;
        String marked = "";
        boolean hadDeletion = false;
        boolean hadInsertion = false;
        while(cur.requestor != null){
            if(cur.requestor.startIndex < cur.startIndex){
                if(cur.requestor.destIndex < cur.destIndex){
                    if(hadDeletion){
                        hadDeletion = false;
                        marked = ">" + marked;
                    }else if(hadInsertion){
                        hadInsertion = false;
                        marked = "[" + marked;
                    }
                    marked = start.substring(cur.requestor.startIndex, cur.startIndex) + marked;
                }else{
                    if(hadInsertion){
                        hadInsertion = false;
                        marked = "[" + marked;
                    }
                    if(!hadDeletion){
                        hadDeletion = true;
                        marked = "<" + marked;
                    }
                    marked = start.substring(cur.requestor.startIndex, cur.startIndex) + marked;
                }
            }else{
                if(hadDeletion){
                    hadDeletion = false;
                    marked = ">" + marked;
                }
                if(!hadInsertion){
                    hadInsertion = true;
                    marked = "]" + marked;
                }
                marked = dest.substring(cur.requestor.destIndex, cur.destIndex) + marked;
            }
            cur = cur.requestor;
        }
        if(hadDeletion){
            hadDeletion = false;
            marked = ">" + marked;
        }else if(hadInsertion){
            hadInsertion = false;
            marked = "[" + marked;
        }
        return new EditDistance(bestFinished.changesToHere, marked);
    }

    private static final int NULL = 0;
    private static final int DEL = 1;
    private static final int INS = 2;

    private static EditDistance get(Map<Integer, Map<Integer, EditDistance>> map, int x, int y){
        Map<Integer, EditDistance> src = map.get(x);
        if (src == null) {
            return null;
        } else {
            return src.get(y);
        }
    }

    private static void put(Map<Integer, Map<Integer, EditDistance>> map, int x, int y, EditDistance d){
        Map<Integer, EditDistance> dest= map.get(x);
        if (dest != null) {
            dest = new HashMap<>();
            map.put(x, dest);
        }
        dest.put(y, d);
    }
    public static EditDistance editDistanceDynamic(String start, String dest){
        final Map<Integer, Map<Integer, EditDistance>> workspace = new HashMap<>();
        Deque<Integer> operations = new LinkedList<>();
        Deque<Integer> lengthOfNulls = new LinkedList<>();
        operations.push(NULL);
        lengthOfNulls.push(0);
        int startIndex = 0;
        int destIndex = 0;
        while(get(workspace, 0, 0)  == null){
            //System.out.println("startIndex: " + startIndex + " destIndex:" + destIndex);
            //System.out.println("x: " + x + " y:" + y);
            boolean toPop = false;
            if(startIndex == start.length() && destIndex == dest.length()){
                put(workspace, startIndex, destIndex, new EditDistance(0, ""));
                toPop = true;
            } else if(startIndex == start.length()){
                put(workspace, startIndex, destIndex, new EditDistance(dest.length() - destIndex, "["
                        + dest.substring(destIndex) + "]"));
                toPop = true;
            } else if(destIndex == dest.length()){
                put(workspace, startIndex, destIndex, new EditDistance(start.length() - startIndex, ">"
                        + start.substring(startIndex) + "<"));
                toPop = true;
            } else if(start.charAt(startIndex) == dest.charAt(destIndex)){
                int originalIndex = startIndex;
                int used = 0;
                while(startIndex < start.length() && destIndex < dest.length() && start.charAt(startIndex) == dest.charAt(destIndex)){
                    used++;
                    startIndex++;
                    destIndex++;
                }
                EditDistance target = get(workspace, startIndex, destIndex);
                if(target == null){
                    //System.out.println(start.substring(originalIndex, startIndex));
                    operations.push(NULL);
                    lengthOfNulls.push(used);
                }else{
                    //System.out.println(start.substring(originalIndex, startIndex));
                    destIndex-=used;
                    put(workspace, originalIndex, destIndex, new EditDistance(target.changes,
                                start.substring(originalIndex, startIndex) + target.marked));
                    toPop = true;
                    startIndex-=used;
                }
            }else{
                EditDistance del = get(workspace, startIndex + 1, destIndex);
                EditDistance ins = get(workspace, startIndex, destIndex + 1);
                if(start.length() - startIndex >= dest.length() - destIndex){
                    if(del == null){
                        operations.push(DEL);
                        startIndex++;
                    }else if(del.changes != 0 && ins == null){
                        operations.push(INS);
                        destIndex++;
                    }else
                        toPop = true;
                }else{
                    if(ins == null){
                        operations.push(INS);
                        destIndex++;
                    }else if(ins.changes != 0 && del == null){
                        operations.push(DEL);
                        startIndex++;
                    }else
                        toPop = true;
                }
                if(toPop){
                    if(del != null && (ins == null || del.changes <= ins.changes)){
                        String marked = del.marked;
                        if(marked.charAt(0) == '>')
                            marked = ">" + start.charAt(startIndex) + marked.substring(1);
                        else
                            marked = ">" + start.charAt(startIndex) + "<" + marked;
                        put(workspace, startIndex, destIndex, new EditDistance(del.changes + 1, marked));
                    }else{
                        String marked = ins.marked;
                        if(marked.charAt(0) == '[')
                            marked = "[" + dest.charAt(destIndex) + marked.substring(1);
                        else
                            marked = "[" + dest.charAt(destIndex) + "]" + marked;
                        put(workspace, startIndex, destIndex, new EditDistance(ins.changes + 1, marked));
                    }
                }
            }
            if(toPop){
                int operation = operations.pop();
                if(operation == INS){
                    destIndex--;
                }else if(operation == DEL){
                    startIndex--;
                }else{
                    int charsUsed = lengthOfNulls.pop();
                    startIndex -= charsUsed;
                    destIndex -= charsUsed;
                }
            }
        }
        return get(workspace, 0, 0);
    }

    /**
     * Note doesn't work on strings containing any of "><[]"
     * @param start 
     * @param destination   
     * @return  
     */
    public static EditDistance editDistanceRecursive(String start, String destination){
        return editDistance(start, destination, new EditDistance[start.length() + 1][destination.length() + 1]);
    }

    private static EditDistance editDistance(String start, String destination, EditDistance[][] workspace){
        if(workspace[start.length()][destination.length()] != null)
            return workspace[start.length()][destination.length()];
        EditDistance ans;
        if(start.length() > 0 && destination.length() == 0){
            ans = new EditDistance(start.length(), ">" + start + "<");
        }else if(start.length() == 0 && destination.length() > 0){
            ans = new EditDistance(destination.length(), "[" + destination + "]");
        }else if(start.length() == 0 && destination.length() == 0){
            ans = new EditDistance(0, "");
        }else if(start.charAt(0) == destination.charAt(0)){
            EditDistance previous = editDistance(start.substring(1), destination.substring(1), workspace);
            ans = new EditDistance(previous.changes, start.charAt(0) + previous.marked);
        }else{
            EditDistance deletion = editDistance(start.substring(1), destination, workspace);
            String marked = null;
            if(deletion.marked.startsWith(">")){
                marked = ">" + start.charAt(0) + deletion.marked.substring(1);
            }else{
                marked = ">" + start.charAt(0) + "<" + deletion.marked;
            }
            deletion = new EditDistance(deletion.changes + 1, marked);
            ans = deletion;
            if(ans.changes > 1){
                EditDistance insertion = editDistance(start, destination.substring(1), workspace);
                if(insertion.marked.startsWith("[")){
                    marked = "[" + destination.charAt(0) + insertion.marked.substring(1);
                }else{
                    marked = "[" + destination.charAt(0) + "]" + insertion.marked;
                }
                insertion = new EditDistance(insertion.changes + 1, marked);
                if(insertion.changes < ans.changes)
                    ans = insertion;
            }
        }
        workspace[start.length()][destination.length()] = ans;
        return ans;
    }

    public String toString(){
        return "(changes: " + changes + ", " + marked + ")";
    }
}
