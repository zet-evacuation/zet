/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */


package algo.graph.nashflow;

import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author schenker
 */
public class LabelMatrix {

   private double[][] coeffmatrix1;   //for sum_k:k<i  l_k u(E1(Xi,Xk))
   private double[][] coeffmatrix2;  //for sum_j:j>i l_j u(E(Xi,Xj))
   private double[][] coeffmatrix3;  //for sum_k:k<i l_i u(E(Xk,Xi))
   private double[][] coeffmatrix4;  //for sum_j:j>i l_i u(E1(Xj,Xi))
   private double[][] coefficientMatrix;

   private double[] rhs;  //rhs for linear equality system


   private List<Node>[] nodePartitionArray;
   private HashMap<Node,Integer> nodeToArrayPosition;
   private List<Edge> edges;
   private List<Edge> E1;
   private IdentifiableDoubleMapping<Edge> edgeCapacities;
   private IdentifiableDoubleMapping<Node> nodeDemands;


    public LabelMatrix(NodePartition nodePart, List<Edge> edgeSet, List<Edge> E_1, IdentifiableDoubleMapping<Edge> edgeCap,
            IdentifiableDoubleMapping<Node> nodedemands) {
        nodePartitionArray = nodePart.getPartitionArray();
        nodeToArrayPosition = nodePart.getNodeToArrayPosition();
        edges = edgeSet;

        E1 = E_1;
        System.out.println("E1 = " + E1.toString());
        edgeCapacities = edgeCap;
        nodeDemands = nodedemands;

        int size = nodePartitionArray.length;
        coeffmatrix1 = new double[size][size];
        coeffmatrix2 = new double[size][size];
        coeffmatrix3 = new double[size][size];
        coeffmatrix4 = new double[size][size];
        coefficientMatrix = new double[size][size];
        rhs = new double[size];


    }

    private void printMatrix(double[][] ar) {
        for(int i=0; i<ar.length;i++) {
            for(int j=0; j<ar.length;j++)
               System.out.print(ar[i][j] + " ");
            System.out.println();
        }
    }

    public void computeCoefficientMatrix() {
        computeCoeffMatrix1();
        computeCoeffMatrix2();
        computeCoeffMatrix3();
        computeCoeffMatrix4();

        System.out.println("matrix1");
        printMatrix(coeffmatrix1);
        System.out.println("matrix2");
        printMatrix(coeffmatrix2);
        System.out.println("matrix3");
        printMatrix(coeffmatrix3);
        System.out.println("matrix4");
        printMatrix(coeffmatrix4);




        //compute coefficientMatrix
        for(int i=0;i<coeffmatrix1.length;i++)
            for(int j=0;j<coeffmatrix1.length;j++)
                coefficientMatrix[i][j]=coeffmatrix1[i][j]+coeffmatrix2[i][j]-coeffmatrix3[i][j]-coeffmatrix4[i][j];


        //compute RHS of linear equality system
        computeRHS();



    }

    public double[] getRHS() {
        return rhs;
    }

    private void computeRHS() {
        for(int i=0; i<rhs.length;i++)
            rhs[i] = setNodePartitionSetDemand(i);
    }

    private double setNodePartitionSetDemand(int i) {
        double demand = 0.0;
        for(Node n: nodePartitionArray[i])
            demand += nodeDemands.get(n);
        return demand;
    }

    public double[][] getCoeffMatrix() {
        return coefficientMatrix;
    }

    private void computeCoeffMatrix1(){

        for(int i = 0; i<coeffmatrix1.length;i++) { //for-loop responsible for running through l_i (column in matrix1)

            for(int k=0;k<coeffmatrix1.length;k++) {//for-loop responsible for creating row of l_i in matrix1
                if(k<i) {   // sum_k : k<i
                    coeffmatrix1[i][k] = getCapacityE1(i,k);  //i-th row, k-th column
                }
            }
       }
    }


    private double getCapacityE1(int i, int k) {
        double cap = 0.0;

        for(Edge e: E1)
            if((nodeToArrayPosition.get(e.start())==i) && (nodeToArrayPosition.get(e.end())==k))
                cap += edgeCapacities.get(e);

        return cap;
    }


    private void computeCoeffMatrix2() {
        for(int i=0; i<coeffmatrix2.length; i++) { //for-loop responsible for running through l_i

            for(int j=0;j<coeffmatrix2.length;j++) { //for-loop responsible for creating row of l_i in matrix2
                if(i<j) { //sum_j:j>i
                    coeffmatrix2[i][j] = getCapacityE(i,j); //i-th row, j-th column

                }
            }
        }
    }

    private double getCapacityE(int i, int j) {
        double cap = 0.0;

        for(Edge e: edges)
            if((nodeToArrayPosition.get(e.start())==i) && (nodeToArrayPosition.get(e.end())==j))
                cap += edgeCapacities.get(e);

        return cap;
    }

    private void computeCoeffMatrix3() {
        for(int i=0;i<coeffmatrix3.length;i++) { //for-loop responsible for running through l_i

            for(int k=0;k<coeffmatrix3.length;k++) { //for-loop responsible for creating row of l_i in matrix3
                if(k<i) //sum_k: k<i
                    coeffmatrix3[i][i] += getCapacityE(k,i);

                }
        }
    }

    private void computeCoeffMatrix4() {
        for(int i=0; i<coeffmatrix4.length;i++) { //for-loop responsible for running through l_i

                for(int j=0;j<coeffmatrix4.length;j++) { //for-loop responsible for creating row of l_i in matrix4
                   if(j>i) //sum_j: j>i
                       coeffmatrix4[i][i] += getCapacityE1(j,i);
                }
        }
    }

}
