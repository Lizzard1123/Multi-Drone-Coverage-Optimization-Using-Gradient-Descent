// Javascript implementation of the above approach.
class Solution {

    constructor() {

        // cost matrix
        this.cost = new Array(31);
        for (let i = 0; i < 31; i++) {
            this.cost[i] = new Array(31).fill(0);
        }

        this.n = 0; //n workers and n jobs
        this.max_match = 0; //n workers and n jobs
        this.lx = new Array(31).fill(0); //labels of X and Y parts
        this.ly = new Array(31).fill(0); //labels of X and Y parts
        this.xy = new Array(31).fill(0); //xy[x] - vertex that is matched with x,
        this.yx = new Array(31).fill(0); //yx[y] - vertex that is matched with y
        this.S = new Array(31).fill(false); //sets S and T in algorithm
        this.T = new Array(31).fill(false); //sets S and T in algorithm
        this.slack = new Array(31).fill(0); //as in the algorithm description
        this.slackx = new Array(31).fill(0); //slackx[y] such a vertex, that
        this.prev_ious = new Array(31).fill(0); //array for memorizing alternating p

    }

    init_labels() {
        for (let i = 0; i < this.lx.length; i++) this.lx[i] = 0;
        for (let i = 0; i < this.ly.length; i++) this.ly[i] = 0;

        for (let x = 0; x < this.n; x++)
            for (let y = 0; y < this.n; y++)
                this.lx[x] = Math.max(this.lx[x], this.cost[x][y]);
    }


    update_labels() {
        let x = 0;
        let y = 0;
        let delta = 99999999; //init delta as infinity

        for (y = 0; y < this.n; y++) //calculate delta using slack
            if (!this.T[y])
                delta = Math.min(delta, this.slack[y]);
        for (x = 0; x < this.n; x++) //update X labels
            if (this.S[x])
                this.lx[x] -= delta;
        for (y = 0; y < this.n; y++) //update Y labels
            if (this.T[y])
                this.ly[y] += delta;
        for (y = 0; y < this.n; y++) //update slack array
            if (!this.T[y])
            this.slack[y] -= delta;
    }


    add_to_tree(x, prev_iousx)
        //x - current vertex,prev_iousx - vertex from X before x in the alternating path,
        //so we add edges (prev_iousx, xy[x]), (xy[x], x)
        {
            this.S[x] = true; //add x to S
            this.prev_ious[x] = prev_iousx; //we need this when augmenting
            for (let y = 0; y < this.n; y++) //update slacks, because we add new vertex to S
                if (this.lx[x] + this.ly[y] - this.cost[x][y] < this.slack[y]) {
                this.slack[y] = this.lx[x] + this.ly[y] - this.cost[x][y];
                this.slackx[y] = x;
            }
        }



    augment() //main function of the algorithm
        {
            if (this.max_match == this.n) return; //check whether matching is already perfect
            let x = 0;
            let y = 0;
            let root = 0; //just counters and root vertex
            let q = new Array(31);
            let wr = 0;
            let rd = 0; //q - queue for bfs, wr,rd - write and read
            //pos in queue
            for (let i = 0; i < this.S.length; i++) this.S[i] = false; //init set S
            for (let i = 0; i < this.T.length; i++) this.T[i] = false; //init set T
            for (let i = 0; i < this.prev_ious.length; i++) this.prev_ious[i] = -1; //init set prev_ious - for the alternating tree


            for (x = 0; x < this.n; x++) //finding root of the tree
            {
                if (this.xy[x] == -1) {
                    q[wr++] = root = x;
                    this.prev_ious[x] = -2;
                    this.S[x] = true;
                    break;
                }
            }

            for (y = 0; y < this.n; y++) //initializing slack array
            {
                this.slack[y] = this.lx[root] + this.ly[y] - this.cost[root][y];
                this.slackx[y] = root;
            }

            //second part of augment() function
            while (true) //main cycle
            {
                while (rd < wr) //building tree with bfs cycle
                {
                    x = q[rd++]; //current vertex from X part
                    for (y = 0; y < this.n; y++) //iterate through all edges in equality graph
                        if (this.cost[x][y] == this.lx[x] + this.ly[y] && !this.T[y]) {
                            if (this.yx[y] == -1) break; //an exposed vertex in Y found, so
                            //augmenting path exists!
                            this.T[y] = true; //else just add y to T,
                            q[wr++] = this.yx[y]; //add vertex yx[y], which is matched
                            //with y, to the queue
                            this.add_to_tree(this.yx[y], x); //add edges (x,y) and (y,yx[y]) to the tree
                        }
                    if (y < this.n)
                        break; //augmenting path found!
                }
                if (y < this.n)
                    break; //augmenting path found!

                this.update_labels(); //augmenting path not found, so improve labeling

                wr = rd = 0;
                for (y = 0; y < this.n; y++)
                // in this cycle we add edges that were added to the equality graph as a
                // result of improving the labeling, we add edge (slackx[y], y) to the tree if
                // and only if !T[y] && slack[y] == 0, also with this edge we add another one
                // (y, yx[y]) or augment the matching, if y was exposed
                    if (!this.T[y] && this.slack[y] == 0) {
                        if (this.yx[y] == -1) //exposed vertex in Y found - augmenting path exists!
                        {
                            x = this.slackx[y];
                            break;
                        } else {
                            this.T[y] = true; //else just add y to T,
                            if (!this.S[this.yx[y]]) {
                                q[wr++] = this.yx[y]; //add vertex yx[y], which is matched with
                                //y, to the queue
                                this.add_to_tree(this.yx[y], this.slackx[y]); //and add edges (x,y) and (y,
                                //yx[y]) to the tree
                            }
                        }
                    }
                if (y < this.n) break; //augmenting path found!
            }

            if (y < this.n) //we found augmenting path!
            {
                this.max_match++; //increment matching
                // in this cycle we inverse edges along augmenting path
                for (let cx = x, cy = y, ty; cx != -2; cx = this.prev_ious[cx], cy = ty) {
                    ty = this.xy[cx];
                    this.yx[cy] = cx;
                    this.xy[cx] = cy;
                }
                this.augment(); //recall function, go to step 1 of the algorithm
            }
        } // end of augment() function

    hungarian() {
        let ret = 0; //weight of the optimal matching
        this.max_match = 0; //number of vertices in current matching
        for (let i = 0; i < this.xy.length; i++) this.xy[i] = -1;
        for (let i = 0; i < this.yx.length; i++) this.yx[i] = -1;
        this.init_labels(); //step 0
        this.augment(); //steps 1-3

        for (let x = 0; x < this.n; x++) //forming answer there
            ret += this.cost[x][this.xy[x]];

        return [ret, this.xy];
    }

    assignmentProblem(Arr, N) {

        this.n = N;
        for (let i = 0; i < this.n; i++)
            for (let j = 0; j < this.n; j++)
                this.cost[i][j] = -1 * Arr[i * this.n + j];

        let ans = this.hungarian();

        return ans;
    }
};


// let n = 3;
// let Arr = [1500, 4000, 4500, 2000, 6000, 3500, 2000, 4000, 2500];
// /*1500 4000 4500
// 															2000 6000 3500
// 															2000 4000 2500*/
// let ob = new Solution();
// console.log(ob.assignmentProblem(Arr, n));

// The code is contributed by Arushi Jindal.