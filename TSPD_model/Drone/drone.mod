/*********************************************
 * OPL 12.9.0.0 Model
 * Author: SS
 * Creation Date: Oct 18, 2019 at 11:24:57 PM
 *********************************************/
/*********************************************
 * OPL 12.9.0.0 Model
 * Author: SS
 * Creation Date: Oct 18, 2019 at 1:26:38 PM
 *********************************************/
using CP;
           
int n = ...;
tuple time {int i; int j; int value;};
{time} w = { <i,j, ftoi(abs(i-j))> | i in 0..n, j in 0..n };
//{time} w = ...;
int truckTime[0..n+1]=...;
int droneTime[0..n]=...;
dvar interval tVisit[i in 0..n+1]optional size truckTime[i]; 
dvar interval dVisit[i in 0..n]optional size droneTime[i];
dvar interval dVisit_before[0..n]optional;
dvar interval dVisit_after[0..n]optional; 
dvar interval visit[i in 0..n];
dvar interval dtVisit[0..n][0..n]optional;
dvar interval tdVisit[0..n][0..n]optional;
dvar sequence tVisit_seq in tVisit; 
dvar sequence dVisit_seq in dVisit; 

minimize endOf(tVisit[n]); 	 
subject to {

//	forall (i in 1..n-2) {
//    	presenceOf(dVisit[i]) => false;
//    	presenceOf(dVisit_before[i]) => false;
//    	presenceOf(dVisit_after[i]) => false;
//  	}

	forall (i in 0..n) presenceOf(visit[i]);
	presenceOf(tVisit[0]);
	presenceOf(tVisit[n+1]);
	first(tVisit_seq, tVisit[0]);
	last(tVisit_seq, tVisit[n]);
	noOverlap(tVisit_seq, w);
	noOverlap(dVisit_seq);
	forall(i in 0..n) {
			alternative(visit[i], append(tVisit[i], dVisit[i]));
			alternative(dVisit_before[i], (all (j in 0..n) tdVisit[i][j]));
			alternative(dVisit_after[i], (all (j in 0..n) dtVisit[i][j]));
			span(dVisit[i], append(dVisit_before[i], dVisit_after[i]));
			endAtStart(dVisit_before[i], dVisit_after[i]);
			(lengthOf(dVisit[i])!=0) == presenceOf(dVisit_before[i]);
			(lengthOf(dVisit[i])!=0) == presenceOf(dVisit_after[i]);   								
	}			
	forall(i, j in 0..n) {
		startBeforeStart(tVisit[j], tdVisit[i][j]);
		startBeforeEnd(tdVisit[i][j], tVisit[j]);
		endBeforeEnd(dtVisit[i][j], tVisit[j]);
	}
} 
