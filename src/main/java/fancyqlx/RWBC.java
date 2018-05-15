package fancyqlx;

import java.util.*;

import Graph;

public class RWBC{
    private Graph g;
    // A map for storing hop from neighbors
    //private Map<Integer, Queue<RWBCMessage>> outMsg;
    private Map<Map<Integer,Integer>, Integer> numPass;
    private Map<Integer, ArrayList<RW>> holdRW; //The number of RW each node holds 

    private int round = 0; // round compleixty counter

    private Integer k; //num of random walks on each node
    private Integer l; //length of random walks

    private Random rand = new Random(47);

    RWBC(Graph g, Integer k, Integer l){
        this.g = g;
        this.k = k;
        this.l = l;
        //outMsg = new HashMap<>();
        numPass = new HashMap<>();
        holdRW = new HashMap<>();
    }

    public void run(){
        int n = g.getN();
        int m = g.getM();
        int  targetID= rand.nextInt(n); //Choose a node as target node
        
        for(Vertex v:g.getVertices()){
            if(v.getID() != targetID){
                holdRW.put(v.getID(),new ArrayList<RW>());
                for(int i=0; i<k; i++){
                    holdRW.get(v.getID()).add(new RW(v,l));
                }
            }
            for(Vertex u:g.getVertices()){
                if(u.getID() != v.getID()){
                    Map<Integer, Integer> nodePair = new HashMap<>();
                    nodePair.put(v.getID(), u.getID());
                    numPass.put(nodePair, 0);
                }
            }
        }

        boolean flag = true;
        while(flag){
            Map<Integer, ArrayList<RW>> newHoldRW = new HashMap<>();
            for(Vertex v:g.getVertices()){
            	if(v.getID() == targetID)
            		continue;
                Map<Integer, Integer> numToNeighbor = new HashMap<>();
                if(holdRW.get(v.getID()) != null) {
	                for(RW rw:holdRW.get(v.getID())){
	                    if(rw.length > 0){
	                        Set<Integer> neighbors = v.getNeighbors();
	                        int rn = rand.nextInt(neighbors.size());
	                        int randNei = -1;
	                        int i=0;
	                        for(Integer id:neighbors){
	                            if(i == rn){
	                                randNei = id;
	                                break;
	                            }
	                            i++;
	                        }
	                        if(randNei != targetID){
	                            if(newHoldRW.get(randNei) == null)
	                                newHoldRW.put(randNei, new ArrayList<RW>());
	                            else
	                                rw.length -= 1;
	                                newHoldRW.get(randNei).add(rw);
	                        }
	                    }
	                }
                }
            }

            holdRW = newHoldRW;
            round++;

            flag = false;
            for(Vertex v:g.getVertices()){
            	if(v.getID() == targetID)
            		continue;
            	if(holdRW.get(v.getID()) != null) {
	                for(RW rw:holdRW.get(v.getID())){
	                    if(rw.length > 0)
	                        flag = true;
	                }
            	}
            }
        }
    }

    public void writeResult(String filepath){
        try{
            BufferedWriter writer = new BufferedWriter((new FileWriter(filepath,true)));
            String s = Integer.toString(g.getN()) + " " + Integer.toString(g.getM()) +
                    " " + Integer.toString(girth) + " " + Integer.toString(phase) +
                    " " + Integer.toString(round) +'\n';
            writer.write(s);
            writer.close();
        }catch (IOException e){

        }
    }
    
    public static void main(String[] args) {
        int n = 100;
        int m = (int)(1.2 * n);
        int w = n;
        for(int i=0;i<20;i++){
            m = (int)(1.2 * n);
            String path = "graphData/graph-"+Integer.toString(n)+
                    "-"+Integer.toString(m)+"-"+Integer.toString(w);
            int B = 1;
            // Defining a new graph
            Graph g = new Graph(B);
            // Constructing graph
            ConstructGraph constructor = new ConstructGraph(path,g);
            if(constructor.construct()){
                // Printing graph
                RWBC alg = new RWBC(g,(int)Math.log(n),n);
                alg.run();
                //System.out.printf("girth = %d\n",alg.getGirth());
                //System.out.printf("phase = %d\n",alg.getPhase());
                System.out.printf("round = %d\n",alg.round);
                String resultFile = "results/RWBC-"+Integer.toString(n)+
                        "-"+Integer.toString(m)+"-"+Integer.toString(w);
                alg.writeResult(resultFile);
                n = n + 100;
                w = n;
            }else{
                break;
            }
        }
    }
}

class RW{
    Vertex source;
    Integer length;

    RW(Vertex s, Integer l){
        source = s;
        length = l;
    }
}