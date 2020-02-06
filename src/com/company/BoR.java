package com.company;

import java.util.Arrays;
import java.util.Scanner;

public class BoR {
    int stake;
    
    public BoR() {
        this.stake = 1;
    }
    
    public void changeStake(int newStake) {
        this.stake = newStake;
    }
    
    public void runGame(int credit, BoR currentGame) {
        String input;
        Scanner sc = new Scanner(System.in);
        int multiplier;
        int origCredit = credit;    //used to check if player won money
        int bookmultiplier = 1;     //used
        int stakeReturn = 0;        //for
        int freegameCounter = 12;   //freegames
        int spinCounter = 0;
        
        System.out.println("Welcome!");
        welcomeMsg(credit, currentGame, spinCounter);
        do {
            input = sc.next();
            
            if (input.equals("spin") || input.equals("s")) {
                //while (spinCounter < 50000) {
                    spinCounter++;
                    multiplier = currentGame.spin();                //returns if won and if so how much
                    credit += currentGame.stake * multiplier * bookmultiplier + stakeReturn;    //sets new credit
                    
                    //Won Book of Ra
                    if (multiplier > 9999) {
                        System.out.println(cColor.CYAN + "BOOK OF RA!!!" + cColor.RESET + "Your next 10 games will be free and every win will get multiplied by 5!!!");
                        bookmultiplier = 5;
                        stakeReturn = currentGame.stake;
                        freegameCounter = 0;
                        credit = origCredit;
                    //Won something
                    } else if (multiplier > 1) {
                        System.out.println("\nYou won " + cColor.CYAN + (credit - origCredit - stakeReturn) + "€" + cColor.RESET + "! Congratulations!\n");
                    //Won stake back during Book of Ra, so multiplied by 5
                    } else if (multiplier == 1 && freegameCounter < 11) {
                        System.out.println("\nYou won " + cColor.CYAN + currentGame.stake * bookmultiplier + "€!" + cColor.RESET + "Congratulations!");
                    //Won stake back
                    } else if (multiplier == 1) {
                        System.out.println("\nYou won your stake back! " + cColor.CYAN + "Try again!\n" + cColor.RESET);
                        credit -= currentGame.stake;
                    //didn´t win nuthing
                    } else {
                        System.out.println("\nSadly, you didn´t win this time! " + cColor.CYAN + "Better luck next time!\n" + cColor.RESET);
                        credit -= currentGame.stake;
                    }
                    welcomeMsg(credit, currentGame, spinCounter);
                    origCredit = credit;
                    freegameCounter++;
                    //detects end of the freegames
                    if (freegameCounter == 11) {
                        System.out.println(cColor.RED + "NO MORE FREEGAMES" + cColor.RESET);
                        stakeReturn = 0;
                        bookmultiplier = 1;
                    }
                //}
            //changes the stake
            } else if (input.equals("stake")) {
                System.out.println("New stake: ");
                currentGame.changeStake(sc.nextInt());
                welcomeMsg(credit, currentGame, spinCounter);
            //catches invalid inputs
            } else if (!input.equals("stop")) {
                System.out.println(cColor.RED + "no valid input" + cColor.RESET);
                welcomeMsg(credit, currentGame, spinCounter);
            }
        } while (!input.equals("stop"));
    } //runs game loop
    
    public static int spin() {
        int[][] rolledSymbols = createArray();
        int comboCounter = 0;
        int wertChecker = 0;
        int finalMultiplier;
        boolean[][] winningSym = new boolean[5][3];
        boolean[][] finalWS;
        int bookCounter = 0;
        
        //region books          //checks for books in the whole array if it finds 3 it returns immediately
        for (int i = 0; i < rolledSymbols.length; i++) {
            for (int j = 0; j < rolledSymbols[i].length; j++) {
                if (rolledSymbols[i][j] == 10) {
                    bookCounter++;
                    winningSym[i][j] = true;
                }
            }
        }
        if (bookCounter >= 3) {
            visualizeArray(rolledSymbols, winningSym);
            return 10000;
        } else {
            resetBool(winningSym);
        }
        //endregion
        //check the winning lines
        //region Gewinnlinie1
        for (int i = 0; i < rolledSymbols.length - 1; i++) {
            if (rolledSymbols[i][0] == rolledSymbols[i + 1][0]) {
                comboCounter++;
                wertChecker += rolledSymbols[i][0];
                winningSym[i][0] = true;
                winningSym[i + 1][0] = true;
            } else {                                        //checks if it was a valid combo, if not resets all tracking variables and cancels loop
                i += rolledSymbols.length - 1 - i;
                if (comboCounter < 2 && wertChecker < 6 || comboCounter < 2 && wertChecker > 7) {
                    comboCounter = 0;
                    wertChecker = 0;
                    resetBool(winningSym);
                }
            }
        }
        finalMultiplier = multiplier(comboCounter, wertChecker);
        finalWS = boolDeepClone(winningSym);
        //endregion
        //region Gewinnlinie 2
        comboCounter = 0;       //resets tracking variabes for new line
        wertChecker = 0;
        resetBool(winningSym);
        
        for (int i = 0; i < rolledSymbols.length - 1; i++) {
            if (rolledSymbols[i][1] == rolledSymbols[i + 1][1]) {
                comboCounter++;
                wertChecker += rolledSymbols[i][1];
                winningSym[i][1] = true;
                winningSym[i + 1][1] = true;
            } else {
                i += rolledSymbols.length - 1 - i;
                if (comboCounter < 2 && wertChecker < 6 || comboCounter < 2 && wertChecker > 7) {
                    comboCounter = 0;
                    wertChecker = 0;
                    resetBool(winningSym);
                }
                
            }
        }
        if (finalMultiplier < multiplier(comboCounter, wertChecker)) {
            finalMultiplier = multiplier(comboCounter, wertChecker);
            finalWS = boolDeepClone(winningSym);
        }
        //endregion
        //region Gewinnlinie 3
        comboCounter = 0;
        wertChecker = 0;
        resetBool(winningSym);
        
        for (int i = 0; i < rolledSymbols.length - 1; i++) {
            if (rolledSymbols[i][2] == rolledSymbols[i + 1][2]) {
                comboCounter++;
                wertChecker += rolledSymbols[i][2];
                winningSym[i][2] = true;
                winningSym[i + 1][2] = true;
            } else {
                i += rolledSymbols.length - 1 - i;
                if (comboCounter < 2 && wertChecker < 6 || comboCounter < 2 && wertChecker > 7) {
                    comboCounter = 0;
                    wertChecker = 0;
                    resetBool(winningSym);
                }
            }
        }
        if (finalMultiplier < multiplier(comboCounter, wertChecker)) {
            finalMultiplier = multiplier(comboCounter, wertChecker);
            finalWS = boolDeepClone(winningSym);
        }
        //endregion
        //region Gewinnlinie4
        comboCounter = 0;
        wertChecker = 0;
        boolean checkContinuity = false;        //line needs multiple if´s, acts as entry barrier if previous symbols were already false
        resetBool(winningSym);
        
        if (rolledSymbols[0][0] == rolledSymbols[1][1]) {
            comboCounter++;
            wertChecker += rolledSymbols[0][0];
            winningSym[0][0] = true;
            winningSym[1][1] = true;
            checkContinuity = true;
        }
        if (checkContinuity) {
            for (int i = 1; i < 3; i++) {
                if (rolledSymbols[i][1] == rolledSymbols[i + 1][1]) {
                    comboCounter++;
                    wertChecker += rolledSymbols[i][1];
                    winningSym[i][1] = true;
                    winningSym[i + 1][1] = true;
                } else {
                    i += rolledSymbols.length - 1 - i;
                    checkContinuity = false;
                    if (comboCounter < 2 && wertChecker < 6 || comboCounter < 2 && wertChecker > 7) {
                        comboCounter = 0;
                        wertChecker = 0;
                        resetBool(winningSym);
                    }
                }
            }
        }
        if (checkContinuity && rolledSymbols[3][1] == rolledSymbols[4][2]) {
            comboCounter++;
            wertChecker += rolledSymbols[3][1];
            winningSym[3][1] = true;
            winningSym[4][2] = true;
        }
        
        if (finalMultiplier < multiplier(comboCounter, wertChecker)) {
            finalMultiplier = multiplier(comboCounter, wertChecker);
            finalWS = boolDeepClone(winningSym);
        }
        //endregion
        //region Gewinnlinie5
        comboCounter = 0;
        wertChecker = 0;
        checkContinuity = false;
        resetBool(winningSym);
        
        if (rolledSymbols[0][2] == rolledSymbols[1][1]) {
            comboCounter++;
            wertChecker += rolledSymbols[0][2];
            winningSym[0][2] = true;
            winningSym[1][1] = true;
            checkContinuity = true;
        }
        if (checkContinuity) {
            for (int i = 1; i < 3; i++) {
                if (rolledSymbols[i][1] == rolledSymbols[i + 1][1]) {
                    comboCounter++;
                    wertChecker += rolledSymbols[i][1];
                    winningSym[i][1] = true;
                    winningSym[i + 1][1] = true;
                } else {
                    i += rolledSymbols.length - 1 - i;
                    checkContinuity = false;
                    if (comboCounter < 2 && wertChecker < 6 || comboCounter < 2 && wertChecker > 7) {
                        comboCounter = 0;
                        wertChecker = 0;
                        resetBool(winningSym);
                    }
                }
            }
        }
        if (checkContinuity && rolledSymbols[3][1] == rolledSymbols[4][0]) {
            comboCounter++;
            wertChecker += rolledSymbols[3][1];
            winningSym[3][1] = true;
            winningSym[4][0] = true;
        }
        if (finalMultiplier < multiplier(comboCounter, wertChecker)) {
            finalMultiplier = multiplier(comboCounter, wertChecker);
            finalWS = boolDeepClone(winningSym);
        }
        //endregion
        visualizeArray(rolledSymbols, finalWS);
        return finalMultiplier;     //highest multiplier gets returned, rest falls away
    }
    
    public static int multiplier(int comboCounter, int wertChecker) {
        if (comboCounter == 1 && wertChecker == 6 || comboCounter == 1 && wertChecker == 7) {
            return 1;
        } else if (comboCounter == 2) {
            switch (wertChecker / comboCounter) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    return 1;
                case 6:
                case 7:
                    return 6;
                case 8:
                    return 12;
                case 9:
                    return 20;
            }
        } else if (comboCounter == 3) {
            switch (wertChecker / comboCounter) {
                case 1:
                case 2:
                case 3:
                    return 4;
                case 4:
                case 5:
                    return 8;
                case 6:
                case 7:
                    return 20;
                case 8:
                    return 80;
                case 9:
                    return 200;
            }
        } else if (comboCounter == 4) {
            switch (wertChecker / comboCounter) {
                case 1:
                case 2:
                case 3:
                    return 15;
                case 4:
                case 5:
                    return 30;
                case 6:
                case 7:
                    return 150;
                case 8:
                    return 400;
                case 9:
                    return 1000;
            }
        } else {
            return 0;
        }
        return 0;
    }   //gets combo and worth, determines the multiplier
    
    public static int[][] createArray() {
        int[][] rolledSymbols = new int[5][3];
        for (int i = 0; i < rolledSymbols.length; i++) {
            for (int j = 0; j < rolledSymbols[i].length; j++) {
                int rndNumber = (int) Math.floor(Math.random() * 100);
    
               /* if(rndNumber <= 13){                //ten
                    rolledSymbols[i][j] = 1;
                }*/
                if (rndNumber <= 16) {              //junker
                    rolledSymbols[i][j] = 2;
                } else if (rndNumber <= 32) {       //queen
                    rolledSymbols[i][j] = 3;
                } else if (rndNumber <= 46) {      //king
                    rolledSymbols[i][j] = 4;
                } else if (rndNumber <= 60) {      //ace
                    rolledSymbols[i][j] = 5;
                } else if (rndNumber <= 70) {     //scarab
                    rolledSymbols[i][j] = 6;
                } else if (rndNumber <= 80) {     //sphinx
                    rolledSymbols[i][j] = 7;
                } else if (rndNumber <= 88) {     //mummy
                    rolledSymbols[i][j] = 8;
                } else if (rndNumber <= 94) {     //explorer
                    rolledSymbols[i][j] = 9;
                } else if (rndNumber <= 100) {     //book
                    rolledSymbols[i][j] = 10;
                }
            }
            
        }
       /*rolledSymbols[0][0] = 3;      //Debugging purposes
        rolledSymbols[1][0] = 3;
        rolledSymbols[2][0] = 3;
        rolledSymbols[3][0] = 3;
        rolledSymbols[4][0] = 1;
        rolledSymbols[0][1] = 2;
        rolledSymbols[1][1] = 3;
        rolledSymbols[2][1] = 2;
        rolledSymbols[3][1] = 3;
        rolledSymbols[4][1] = 4;
        rolledSymbols[0][2] = 5;
        rolledSymbols[1][2] = 3;
        rolledSymbols[2][2] = 6;
        rolledSymbols[3][2] = 2;
        rolledSymbols[4][2] = 4;*/
        return rolledSymbols;
    } //2d int array random values 1-9
    
    public static void visualizeArray(int[][] rolledSymbols, boolean[][] finalWS) {
        for (int i = 0; i < rolledSymbols[i].length; i++) {
            for (int j = 0; j < rolledSymbols.length; j++) {
                if (finalWS[j][i]) {
                    System.out.print(cColor.BLUE);
                }
                switch (rolledSymbols[j][i]) {
                    case 1:
                        System.out.print("ten       ");
                        break;
                    case 2:
                        System.out.print("junker    ");
                        break;
                    case 3:
                        System.out.print("queen     ");
                        break;
                    case 4:
                        System.out.print("king      ");
                        break;
                    case 5:
                        System.out.print("ace       ");
                        break;
                    case 6:
                        System.out.print("scarab    ");
                        break;
                    case 7:
                        System.out.print("sphinx    ");
                        break;
                    case 8:
                        System.out.print("mummy     ");
                        break;
                    case 9:
                        System.out.print("explorer  ");
                        break;
                    case 10:
                        System.out.print("book      ");
                        break;
                    default:
                        System.out.print("fehler    ");
                        break;
                }
                System.out.print(cColor.RESET);
            }
            System.out.println();
        }
    } //prints array | replaces values with respective card names
    
    public static void resetBool(boolean[][] winningSym) {
        
        for (boolean[] row : winningSym) {
            Arrays.fill(row, false);
        }
    }   //resets bool array to all false
    
    public static boolean[][] boolDeepClone(boolean[][] winningSym) {
        boolean[][] finalWS;
        
        finalWS = winningSym.clone();
        for (int i = 0; i < finalWS.length; i++) {
            finalWS[i] = winningSym[i].clone();
        }
        return finalWS;
    } //clones 2d bool array
    
    public static void welcomeMsg(int credit, BoR currentGame, int spincounter) {
        System.out.println(spincounter);
        System.out.println("Write " + cColor.CYAN + "\"spin\"" + cColor.RESET + " to play, " + cColor.YELLOW + "\"stake\"" + cColor.RESET + " to change your stake and " + cColor.RED + "\"stop\"" + cColor.RESET + " to stop!");
        System.out.println("current credit: " + cColor.CYAN + credit + cColor.RESET + "\ncurrent stake: " + cColor.YELLOW + currentGame.stake + cColor.RESET);
    } //display current stake and credit
}
