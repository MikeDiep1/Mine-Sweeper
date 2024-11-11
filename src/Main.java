import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        boolean debug = false;
        int mines = 0;
        Minefield game = null; //since these are not global, we have to set its default values
        System.out.println("Level: Easy, Medium, or Hard");
        Scanner myScanner = new Scanner(System.in);
        String level = myScanner.nextLine();
        if (level.equals("Easy")) {
            game = new Minefield(5, 5, 5); //creates an Easy minefield if the user answered they wanted to play that level
            mines = 5;
        } else if (level.equals("Medium")) {
            game = new Minefield(9, 9, 12);
            mines = 12;
        } else if (level.equals("Hard")) {
            game = new Minefield(20, 20, 40);
            mines = 20;
        }
        System.out.println("Would you like to play in Debug or Normal Mode");
        String mode = myScanner.nextLine();
        if (mode.equals("Debug")) { //changes the debug boolean if the player wants to play on debug mode
            debug = true;
        }
        System.out.println("Initiate the game with your starting coordinates: [x] [y]");
        String start = myScanner.nextLine();
        String[] coordinate = start.split(" "); //turns the player's answer into a string array in order for access
        game.createMines(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]), mines);
        if (!debug) { //if played on Normal mode, printMinefield will not be accessed
            game.revealMines(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]));
            game.evaluateField();
            System.out.println(game.toString());
        } else {
            game.revealMines(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]));
            game.evaluateField();
            game.printMinefield();
            System.out.println(game.toString());
        }
        while (!game.gameOver()) {
            System.out.println("Enter a coordinate and if you wish to place a flag (Remaining: " + game.flagsLeft + "): [x] [y] [f (f or nf)]");
            String guess = myScanner.nextLine();
            String[] play = guess.split(" ");  //turns the player's guess into a string array in order for access
            while (play[2].equals("f") && game.flagsLeft == 0 || Integer.parseInt(play[0]) < 0 || Integer.parseInt(play[1]) < 0
                    || Integer.parseInt(play[0]) >= game.r || Integer.parseInt(play[1]) >= game.col
                || game.getField()[Integer.parseInt(play[0])][Integer.parseInt(play[1])].getRevealed()){ //while the player's answer is invalid, continue to prompt them to re-enter different coordinates
                System.out.println("Re-enter valid coordinates: [x] [y ] [f (f or nf)]");
                guess = myScanner.nextLine();
                play = guess.split(" ");
            }
            if(play[2].equals("f")) { //if player wants to flag
                game.guess(Integer.parseInt(play[0]), Integer.parseInt(play[1]), true);
                if(debug){
                    game.printMinefield();
                }
                System.out.println(game.toString());
            }
            else if(play[2].equals("nf")) { //if no flage
                if(game.guess(Integer.parseInt(play[0]), Integer.parseInt(play[1]), false)){ //and guess is true, player has hit a bomb
                    System.out.println("BOOM GAMEOVER! You Just Blew Up! YOU Suck!");
                    game.printMinefield();
                    return;
                }
                if(game.gameOver()){ //checks if that was winning guess
                    System.out.println("Yay, You Won Nerd. Now go tell your father!");
                }
                if(debug){
                    game.printMinefield();
                }
                System.out.println(game.toString());
            }
        }
        System.out.println("Yay, You Won Nerd. Now go tell your father!");
    }
}
