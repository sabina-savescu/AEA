package algorithms;

import instance.Instance;
import java.util.ArrayList;
import java.util.List;
import util.Distance;
import util.MaxHeap;
import util.MaxHeap.HeapIndexChangedListener;
import util.Operation;
import util.Solution;

public class GreedySolver<E> implements Solver<E>
{
    private  Instance<E> instance;
    private  Solution<E> solution;
    private  Distance<E> driveDist;
    private  Distance<E> flyDist;
    private MaxHeap<SolutionNode> heap;
    private ArrayList<SolutionNode> nodesList;
    
    private enum Label
    {
        SIMPLE, COMBINED, TRUCK, DRONE;
    }

    public GreedySolver(){};

    public GreedySolver(Instance<E> i, Solution<E> sol)
    {
        instance = i;
        driveDist = instance.getDriveDistance();
        flyDist = instance.getFlyDistance();
        this.solution = sol;

        init();
        double res = solve(Double.POSITIVE_INFINITY);
        if (res >= 0)
            solve(res);
    }

    private void init()
    {
        heap = new MaxHeap<SolutionNode>(instance.getNodeCount()+2);
        nodesList = new ArrayList<>(instance.getNodeCount()+1);
        for (Operation<E> op : solution)
                nodesList.add(new SolutionNode(op.getStart()));
        nodesList.add(new SolutionNode(instance.getDepot()));

        nodesList.get(0).makeLink(null, nodesList.get(1));
        for (int i=1; i < nodesList.size() - 1; i++)
                nodesList.get(i).makeLink(nodesList.get(i-1), nodesList.get(i+1));
        nodesList.get(nodesList.size()-1).makeLink(nodesList.get(nodesList.size()-2), null);

        for (SolutionNode sn : nodesList)
                heap.add(sn, sn.MaxCost());
    }


    private double solve(double target)
    { 
        double currentCost = 0;
        double bestCost = 0;
        
        while (heap.size() > 0)
        {
            SolutionNode sn = heap.getKey(0);
            double maxCost = sn.MaxCost();
            
            if ((Double.isInfinite(maxCost)) || (maxCost < 0) || currentCost + maxCost >= target)
            {
                    for (SolutionNode node : nodesList)
                        node.label = Label.COMBINED;
                    break;
            }

            currentCost += maxCost;
            bestCost = Math.max(bestCost, currentCost);
            if (sn.canPushLeft() || sn.canPushRight() || sn.canMakeFly())
                    sn.chooseOperation();         
        }
        if (currentCost == bestCost)
                return -1;
        
        return bestCost;
    }


    public Solution<E> getSolution()
    {
        List<Operation<E>> ops = new ArrayList<>();
        List<E> currentNodesList = null;
        E fly = null;

        for (SolutionNode sn : nodesList){
            switch (sn.label) {
                case COMBINED:
                    if (currentNodesList != null)
                    {
                        currentNodesList.add(sn.element);
                        Operation<E> op = new Operation<E>(currentNodesList,fly);
                        ops.add(op);
                    }   
                    currentNodesList = new ArrayList<>();
                    currentNodesList.add(sn.element);
                    fly = null;
                    break;
                case TRUCK:
                    currentNodesList.add(sn.element);
                    break;
                case DRONE:           
                    fly = sn.element;
                    break;
            }
        }
        if (currentNodesList.size() > 1)
        {
                Operation<E> op = new Operation<E>(currentNodesList,fly);
                ops.add(op);
        }
        return new Solution<>(instance,ops);
    }

    @Override
    public Solution<E> solve(Instance<E> instance, Solution<E> order)
    {
            GreedySolver<E> fdp = new GreedySolver<>(instance, order);
            return fdp.getSolution();
    }


    public class SolutionNode implements HeapIndexChangedListener
    {
        private SolutionNode leftNode;
        private SolutionNode rightNode;
        private SolutionNode flyTo;
        private SolutionNode flyFrom;

        protected int heapIndex;
   
        private Label label;
        private double driveBeforeCost;
        private double driveAfterCost;
        private double flyBeforeCost;
        private double flyAfterCost;
        
        public final E element;

        public SolutionNode(E e)
        {
                element = e;
                label = Label.SIMPLE;
        }

        
        public void makeLink(SolutionNode left, SolutionNode right){
            if (leftNode != null || rightNode != null)
                    throw new IllegalStateException();
            leftNode = left;
            rightNode = right;
        }


        public boolean canMakeFly()
        {
            return label == Label.SIMPLE && leftNode != null && rightNode != null && !instance.isDepot(element);
        }


        public boolean canPushRight()
        {
            return label == Label.SIMPLE && rightNode != null && rightNode.label == Label.COMBINED;
        }


        public boolean canPushLeft()
        {
            return label == Label.SIMPLE && leftNode != null && leftNode.label == Label.COMBINED;
        }

        public double makeFlyCost()
        {
            if (label != Label.SIMPLE)
                    throw new IllegalStateException();

            if (element == null || leftNode == null || rightNode == null)
                    throw new IllegalStateException();

            double currentDist =  driveDist.getContextFreeDistance(leftNode.element,element)
                                    + driveDist.getContextFreeDistance(element, rightNode.element);
            double flyDist = GreedySolver.this.flyDist.getFlyDistance(leftNode.element, rightNode.element, element);
            double driveDist  = GreedySolver.this.driveDist.getContextFreeDistance(leftNode.element, rightNode.element);
            
            return currentDist - Math.max(flyDist, driveDist);
        }

        public void makeFly()
        { 
            if (!canMakeFly())
                    throw new IllegalStateException();

            label = Label.DRONE;

            double d = driveDist.getContextFreeDistance(leftNode.element, rightNode.element);
            leftNode.driveAfterCost = d;
            rightNode.driveBeforeCost = d;

            double f = flyDist.getFlyDistance(leftNode.element, rightNode.element, element);
            leftNode.flyAfterCost = f;
            rightNode.flyBeforeCost = f;

            leftNode.flyTo = this;
            rightNode.flyFrom = this;
            leftNode.rightNode = rightNode;
            rightNode.leftNode = leftNode;
            flyFrom = leftNode;
            flyTo = rightNode;

            heap.remove(heapIndex);
            if (leftNode.label == Label.SIMPLE)
            {
                heap.remove(leftNode.heapIndex);
                leftNode.label = Label.COMBINED;
            }

            if (rightNode.label == Label.SIMPLE)
            {
                heap.remove(rightNode.heapIndex);
                rightNode.label = Label.COMBINED;
            }

            if (leftNode.leftNode != null && leftNode.leftNode.label == Label.SIMPLE)
                heap.update(leftNode.leftNode.heapIndex, leftNode.leftNode.MaxCost());

            if (rightNode.rightNode != null && rightNode.rightNode.label == Label.SIMPLE)
                heap.update(rightNode.rightNode.heapIndex, rightNode.rightNode.MaxCost());
        }


        public double getLeftOperationCost()
        { 
            if (label != Label.COMBINED || flyFrom == null)
                    throw new IllegalStateException();

            return Math.max(driveBeforeCost, flyBeforeCost);
        }


        public double getRightOperationCost()
        {
            if (label != Label.COMBINED || flyTo == null)
                    throw new IllegalStateException();

            return Math.max(driveAfterCost, flyAfterCost);
        }


        public double pushLeftCost()
        { 
            double newDrive = leftNode.driveBeforeCost + driveDist.getContextFreeDistance(leftNode.element, element);
            
            if (Double.isInfinite(leftNode.flyBeforeCost))
                    throw new IllegalStateException();
            
            double newFly = flyDist.getFlyDistance(leftNode.flyFrom.flyFrom.element,
                                                        element,
                                                        leftNode.flyFrom.element);
            return leftNode.getLeftOperationCost() - Math.max(newDrive, newFly);
        }


        public void pushLeft()
        {
            if (!canPushLeft())
                    throw new IllegalStateException();

            double driveBefore = leftNode.driveBeforeCost + driveDist.getContextFreeDistance(leftNode.element, element);
            double flyBefore = flyDist.getFlyDistance(leftNode.flyFrom.flyFrom.element,
                                                                        element,
                                                                        leftNode.flyFrom.element);

            leftNode.label = Label.TRUCK;
            label = Label.COMBINED;
            
            driveBeforeCost = driveBefore;
            flyBeforeCost = flyBefore;
            flyFrom = leftNode.flyFrom;
            flyFrom.flyTo = this;

            flyFrom.flyFrom.flyAfterCost = flyBefore;
            flyFrom.flyFrom.driveAfterCost = driveBefore;

            heap.remove(heapIndex);
            if (rightNode != null && rightNode.label == Label.SIMPLE)
                    heap.update(rightNode.heapIndex, rightNode.MaxCost());
        }


        public double pushRightCost()
        {
            double newDrive = rightNode.driveAfterCost + driveDist.getContextFreeDistance(element, rightNode.element);
            double newFly = flyDist.getFlyDistance(element, rightNode.flyTo.flyTo.element, rightNode.flyTo.element);
            return rightNode.getRightOperationCost() - Math.max(newDrive, newFly);
        }

        public void pushRight()
        {
            if (!canPushRight())
                    throw new IllegalStateException();

            double driveAfter = rightNode.driveAfterCost + driveDist.getContextFreeDistance(element, rightNode.element);
            double flyAfter = flyDist.getFlyDistance(element, rightNode.flyTo.flyTo.element, rightNode.flyTo.element);

            rightNode.label = Label.TRUCK;
            label = Label.COMBINED;
            flyTo = rightNode.flyTo;
            flyTo.flyFrom = this;
            flyTo.flyTo.flyBeforeCost = flyAfter;
            flyTo.flyTo.driveBeforeCost = driveAfter;
            driveAfterCost = driveAfter;
            flyAfterCost = flyAfter;

            heap.remove(heapIndex);
            if (leftNode != null && leftNode.label == Label.SIMPLE)
                    heap.update(leftNode.heapIndex, leftNode.MaxCost());
        }

        public double MaxCost()
        {
            double cost = Double.NEGATIVE_INFINITY;
            if (canMakeFly())
                    cost = Math.max(cost, makeFlyCost());
            if (canPushRight())
                    cost = Math.max(cost, pushRightCost());
            if (canPushLeft())
                    cost = Math.max(cost, pushLeftCost());
            return cost;
        }


        public void chooseOperation()
        {
            double flyCost = Double.NEGATIVE_INFINITY;
            double pushLeftCost = Double.NEGATIVE_INFINITY;
            double pushRightCost = Double.NEGATIVE_INFINITY;

            if (canMakeFly())
                flyCost = makeFlyCost();
            if (canPushLeft())
                pushLeftCost = pushLeftCost();
            if (canPushRight())
                    pushRightCost = pushRightCost();

            if (flyCost >= pushLeftCost && flyCost >= pushRightCost)
                makeFly();
            else if (Double.isFinite(pushLeftCost) && pushLeftCost >= pushRightCost)
                pushLeft();
            else if (Double.isFinite(pushRightCost))
                pushRight();
            else
                throw new IllegalStateException();
        }

        @Override
        public void notifyHeapIndex(int index){
                heapIndex = index;
        }

        @Override
        public String toString(){
                return element.toString()+" ["+label+"]";
        }
    }
 

}
