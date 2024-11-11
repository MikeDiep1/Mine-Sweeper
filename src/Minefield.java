import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Minefield {
    /**
     * Global Section
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";
    private Cell[][] field;
    private int startFlags; //is redundant and can just be flagsLeft
    public int flagsLeft; //flags left to play with
    public int r; //essentially field.length, but I wanted to make it shorter for "for" loops
    public int col; //essentially field[0].length, but I wanted to make it shorter for "for" loops

    /**
     * Constructor
     *
     * @param rows    Number of rows.
     * @param columns Number of columns.
     * @param flags   Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        r = rows;
        col = columns;
        field = new Cell[rows][columns];
        startFlags = flags;
        flagsLeft = startFlags;
        for(int x = 0; x < field.length; x++){
            for(int i = 0; i < field[0].length; i++){ // start off by setting every cell of the field to its defaul value
                field[x][i] = new Cell(false, "0");     //default value set to 0, because evaluate field only checks and increments on mines
                                                                        //therefore in revealZeroes it would be checking dashes as default instead of checking for zeroes
            }
        }
    }

    public Cell[][] getField() { //helper getter function for my main.java
        return field;
    }

    /**
     * evaluateField
     *
     * @function When a mine is found in the field, calculate the surrounding 3x3 tiles values. If a mine is found, increase the count for the square.
     */
    public void evaluateField() {
        for(int x = 0; x < field.length; x++){
            for(int i = 0; i < field[0].length; i++){   //iterates through every cell to detect if its a Mine
                if(field[x][i].getStatus().equals("M")){
                    if(i+1<field[0].length && (!field[x][i + 1].getStatus().equals("M"))) { //checks for bounds and if right of cell is not already a Mine
                        String rValue = field[x][i + 1].getStatus();
                        int rVal = Integer.parseInt(rValue) + 1; // its value is a string and has to be turned into integer to add to
                        field[x][i + 1].setStatus(rVal + ""); //string concatenation to revert back
                    }
                    if(i-1>=0 && (!field[x][i - 1].getStatus().equals("M")) ) { //left check
                        String lValue = field[x][i - 1].getStatus();
                        int lVal = Integer.parseInt(lValue) + 1;
                        field[x][i - 1].setStatus(lVal + "");
                    }
                    if(x-1>=0 && (!field[x - 1][i].getStatus().equals("M"))) { //upper check
                        String uValue = field[x - 1][i].getStatus();
                        int uVal = Integer.parseInt(uValue) + 1;
                        field[x - 1][i].setStatus(uVal + "");
                    }
                    if(x+1<field.length && (!field[x+1][i].getStatus().equals("M"))) { //lower check
                        String lwValue = field[x + 1][i].getStatus();
                        int lwVal = Integer.parseInt(lwValue) + 1;
                        field[x + 1][i].setStatus(lwVal + "");
                    }
                    if(x-1>=0 && i+1<field[0].length && (!field[x - 1][i + 1].getStatus().equals("M"))) { //upper right check
                        String ruValue = field[x - 1][i + 1].getStatus();
                        int ruVal = Integer.parseInt(ruValue) + 1;
                        field[x - 1][i + 1].setStatus(ruVal + "");
                    }
                    if(x-1>=0 && i-1>=0 && (!field[x - 1][i - 1].getStatus().equals("M"))) { //upper left check
                        String luValue = field[x - 1][i - 1].getStatus();
                        int luVal = Integer.parseInt(luValue) + 1;
                        field[x - 1][i - 1].setStatus(luVal + "");
                    }
                    if(x+1<field.length && i+1<field[0].length && (!field[x + 1][i + 1].getStatus().equals("M"))) { //lower right check
                        String rlValue = field[x + 1][i + 1].getStatus();
                        int rlVal = Integer.parseInt(rlValue) + 1;
                        field[x + 1][i + 1].setStatus(rlVal + "");
                    }
                    if(x+1<field.length && i-1>=0 && (!field[x + 1][i - 1].getStatus().equals("M"))) { //lower left check
                        String llValue = field[x + 1][i - 1].getStatus();
                        int llVal = Integer.parseInt(llValue) + 1;
                        field[x + 1][i - 1].setStatus(llVal + "");
                    }
                }
            }
        }
    }

    /**
     * createMines
     *
     * @param x     Start x, avoid placing on this square.
     * @param y     Start y, avoid placing on this square.
     * @param mines Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random rand = new Random();
        int counter = 0;
        while(counter<mines){
            int coord = rand.nextInt(r); //randomizes row coordinate
            int coord2 = rand.nextInt(col); //randomizes column coordinate
            while(coord == x && coord2 == y || field[coord][coord2].getRevealed() || field[coord][coord2].getStatus().equals("M")){
                coord = rand.nextInt(r); //checks the validity to make sure Mine is not already placed or is the starting coordinate
                coord2 = rand.nextInt(col);
            }
            field[coord][coord2].setStatus("M");
            counter++;
        }
    }

    /**
     * guess
     *
     * @param x    The x value the user entered.
     * @param y    The y value the user entered.
     * @param flag A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
            if(flag && flagsLeft>0 && !field[x][y].getStatus().equals("F")){ //checks if validity of player wants to flag
                field[x][y].setStatus("F");
                field[x][y].setRevealed(true);
                flagsLeft--;
            }
            else if(!flag && field[x][y].getStatus().equals("0")){
                revealZeroes(x,y);
            }
            else if(!flag && field[x][y].getStatus().equals("M")){
                return true;
            }
        field[x][y].setRevealed(true);
        return false;
    }

    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        for(int x = 0; x < field.length; x++) {
            for (int i = 0; i < field[0].length; i++) {
                if (!field[x][i].getRevealed()) //checks every cell to see if its not revealed
                    return false;
            }
        }
        return true; //if they're all revealed, game is over
    }

    /**
     * revealField
     * <p>
     * This method should follow the psuedocode given.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        Stack1Gen<int []> stack = new Stack1Gen<int []>();
        stack.push(new int[]{x,y});
        field[x][y].setRevealed(true);
        while(!stack.isEmpty()) { //while the stack is not empty continue to check if the neighbors of the cell is 0
            int[] temp = stack.pop(); //removes the top of the stack and turns it into the temp variable in order to check its neighbors
            field[temp[0]][temp[1]].setRevealed(true);
            if (temp[1] - 1 >= 0 && (field[temp[0]][temp[1]-1].getStatus().equals("0")) && !field[temp[0]][temp[1] - 1].getRevealed()) { //left check
                stack.push(new int[]{temp[0],temp[1]-1});
            }
            if (temp[1] + 1 < field[0].length && (field[temp[0]][temp[1]+1].getStatus().equals("0")) && !field[temp[0]][temp[1] + 1].getRevealed()) { //right check
                stack.push(new int[]{temp[0],temp[1]+1});
            }
            if (temp[0] - 1 >= 0 && (field[temp[0] - 1][temp[1]].getStatus().equals("0")) && !field[temp[0]-1][temp[1]].getRevealed()) { //upper check
                stack.push(new int[]{temp[0]-1,temp[1]});
            }
            if (temp[0] + 1 < field.length && (field[temp[0] + 1][temp[1]].getStatus().equals("0")) && !field[temp[0]+1][temp[1]].getRevealed()) { //lower check
                stack.push(new int[]{temp[0]+1,temp[1]});
            }
        }
    }

    /**
     * revealMines
     * <p>
     * This method should follow the psuedocode given.
     * Why might a queue be useful for this function?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealMines(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen<>();
        int count =0;
        queue.add(new int [] {x, y});
        while(queue.length()!=0){ //while the queue is not empty remove a cell and instantiate it as a temporary value in order to check its neighbor
            int [] temp = queue.remove();
            field[temp[0]][temp[1]].setRevealed(true);
            if(field[temp[0]][temp[1]].getStatus().equals("M")){
                return;
            }
            if (temp[1] - 1 >= 0 && !field[temp[0]][temp[1] - 1].getRevealed()) { //left check
                queue.add(new int[]{temp[0],temp[1]-1});
            }
            if (temp[1] + 1 < field[0].length && !field[temp[0]][temp[1] + 1].getRevealed()) { //right check
                queue.add(new int[]{temp[0],temp[1]+1});
            }
            if (temp[0] - 1 >= 0 && !field[temp[0]-1][temp[1]].getRevealed()) { //upper check
                queue.add(new int[]{temp[0]-1,temp[1]});
            }
            if (temp[0] + 1 < field.length && !field[temp[0]+1][temp[1]].getRevealed()) { //lower check
                queue.add(new int[]{temp[0]+1,temp[1]});
            }
        }
    }

    /**
     * revealStart
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealStart(int x, int y) {

    }

    /**
     * printMinefield
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void printMinefield() {
        System.out.print(" ");
        for (int j = 0; j < field[0].length; j++) { //sets the top template
            System.out.print("  " + j);
        }
        for (int i = 0; i < r; i++) {
            System.out.print("\n" + i); //sets the rows template
            for (int x = 0; x < col; x++) {
                if(x<=10) {

                    if (field[i][x].getStatus().equals("M")) {
                        System.out.print("  " + ANSI_RED + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("F")) {
                        System.out.print("  " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("0")) {
                        System.out.print("  " + ANSI_BLUE + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("1")) {
                        System.out.print("  " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("2")) {
                        System.out.print("  " + ANSI_YELLOW + field[i][x].getStatus() + ANSI_GREY_BG);
                    } else {
                        System.out.print("  " + ANSI_RED_BRIGHT + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                }
                else{ //if the column is larger than ten then correct the spacing by adding an extra space
                    if (field[i][x].getStatus().equals("M")) {
                        System.out.print("   " + ANSI_RED + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("F")) {
                        System.out.print("   " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("0")) {
                        System.out.print("   " + ANSI_BLUE + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("1")) {
                        System.out.print("   " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                    else if (field[i][x].getStatus().equals("2")) {
                        System.out.print("   " + ANSI_YELLOW + field[i][x].getStatus() + ANSI_GREY_BG);
                    } else {
                        System.out.print("   " + ANSI_RED_BRIGHT + field[i][x].getStatus() + ANSI_GREY_BG);
                    }
                }
            }
        }
        System.out.println("\n");
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        String myField = " ";
        for (int j = 0; j < field[0].length; j++) { //sets the top template
            myField += "  " + j;
        }
        for (int i = 0; i < field.length; i++) {
            myField += "\n" + i; //sets the row template
            for (int x = 0; x < field[i].length; x++) {
                if(!field[i][x].getRevealed()){
                    if(x>10){ //if the column is larger than 10 and is not revealed, add an extra space and default dash
                        myField += "   -";
                    }
                    else{
                        myField += "  -";
                    }
                }

                else if(x<=10) {
                    if (field[i][x].getStatus().equals("M")) {
                        myField += "  " + ANSI_RED + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("F")) {
                        myField += "  " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("0")) {
                        myField += "  " + ANSI_BLUE + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("1")) {
                        myField += "  " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("2")) {
                        myField += "  " + ANSI_YELLOW + field[i][x].getStatus() + ANSI_GREY_BG;
                    } else {
                        myField += "  " + ANSI_RED_BRIGHT + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                }
                else{
                    if (field[i][x].getStatus().equals("M")) {
                        myField += "   " + ANSI_RED + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("F")) {
                        myField += "   " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("0")) {
                        myField += "   " + ANSI_BLUE + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("1")) {
                        myField += "   " + ANSI_GREEN + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                    else if (field[i][x].getStatus().equals("2")) {
                        myField += "   " + ANSI_YELLOW + field[i][x].getStatus() + ANSI_GREY_BG;
                    } else {
                        myField += "   " + ANSI_RED_BRIGHT + field[i][x].getStatus() + ANSI_GREY_BG;
                    }
                }
            }
        }
        return myField.toString();
    }
}