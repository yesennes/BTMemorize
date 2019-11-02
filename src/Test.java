/**
 *
 * @author Luke Senseney
 * @version 1.0 Mar 29, 19
 **/
public class Test {
    public static void main(String[] args){
        //EditDistance resut = EditDistance.editDistance("And why not do evil that good may come, as some people slanderously charge us with saying. Their condemnation is just.", "And why not do evil that good may come?-as some people slanderously charge us with saying. Their condemnation is just.", 12);
        EditDistance resut = EditDistance.editDistance("What if som were unfaithful?", "What if some were unfaithful? Does their faithlessness nullify the faithfulness of God?");
        System.out.println(resut);
    }
}
