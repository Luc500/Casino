package com.company;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        int credit;
        BoR currentGame = new BoR();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Your deposit: ");
        credit = sc.nextInt();
        currentGame.runGame(credit, currentGame);
        
    }
}