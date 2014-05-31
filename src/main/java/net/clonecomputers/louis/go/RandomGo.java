package net.clonecomputers.louis.go;

import static java.lang.Math.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RandomGo {
	
	private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));
	
	private static boolean gameOn = true;
	
	private static final int[][] board = new int[19][19];
	
	private static int numMoves = 0;
	
	private static int lastMoveX = -1;
	
	private static int lastMoveY = -1;
	
	public static void main(String[] args) throws IOException {
		String input = "";
		while(!input.equalsIgnoreCase("black") && !input.equalsIgnoreCase("white")) {
			System.out.println("Which color are we playing as?");
			System.out.print(">");
			input = IN.readLine();
		}
		if(input.equalsIgnoreCase("white")) {
			getOpponentsMove();
			numMoves++;
		}
		while(gameOn) {
			makeMove();
			numMoves++;
			getOpponentsMove();
			numMoves++;
		}
	}
	
	private static void makeMove() throws IOException {
		double totalWeight = 0;
		double[][] moveWeights = new double[19][19];
		for(int i = 0; i < moveWeights.length; ++i) {
			for(int j = 0; j < moveWeights[i].length; ++j) {
				if(board[i][j] == 0) {
					double addition = calcMyBMDistr(i, j, numMoves+1);
					if(lastMoveX >= 0) addition += adjustForOppMove(lastMoveX-i, lastMoveY-j);
					moveWeights[i][j] += addition;
					totalWeight += addition;
				} else {
					for(int k = 0; k < moveWeights.length; ++k) {
						for(int l = 0; l < moveWeights[k].length; ++l) {
							if(board[k][l] != 0) continue;
							double addition = adjustForOppMove(i-k, j-l)/8;
							moveWeights[k][l] += addition;
							totalWeight += addition;
						}
					}
				}
			}
		}
		while(true) {
			double choice = random()*totalWeight;
			int moveX = 0;
			int moveY = 0;
			double sum = 0;
			outside:
			for(; moveX < moveWeights.length; ++moveX) {
				for(; moveY < moveWeights[moveX].length; ++moveY) {
					sum += moveWeights[moveX][moveY];
					if(sum > choice) break outside;
				}
				moveY = 0;
			}
			System.out.println("Is (" + (moveX + 1) + "," + (moveY + 1) + ") valid (y/N)?");
			if(IN.readLine().equalsIgnoreCase("y")) {
				board[moveX][moveY] = 1;
				break;
			}
			totalWeight -= moveWeights[moveX][moveY];
			moveWeights[moveX][moveY] = 0;
		}
	}
	
	private static double adjustForOppMove(int dx, int dy) {
		double d = hypot(dx, dy);
		return 0.8665*pow(d, -2) - 0.8165*pow(d, -2.1225);
	}
	
	private static double calcMyBMDistr(int x, int y, double temp) {
		return calcBMDistr(x, temp) + calcBMDistr(18-x, temp) +
			   calcBMDistr(y, temp) + calcBMDistr(18-y, temp);
	}
	
	private static double calcBMDistr(int x, double temp) {
		return sqrt(2/PI)*pow(x,2)*exp(-pow(x, 2)/sqrt(8*temp))/(temp*sqrt(512*temp));
	}
	
	private static void getOpponentsMove() throws IOException {
		boolean isValid = false;
		boolean gettingCaptureList = false;
		while(!isValid) {
			if(!gettingCaptureList) {
				System.out.println("Enter location of opponent's move:");
				System.out.print(">");
				String[] input = IN.readLine().split(" ");
				if(input.length < 1) continue;
				if(input[0].equalsIgnoreCase("pass")) {
					lastMoveX = -1;
					isValid = true;
				}
				if(input.length < 2) continue;
				try {
					int x = Integer.parseInt(input[0])-1;
					int y = Integer.parseInt(input[1])-1;
					if(board[x][y] != 0) continue;
					board[x][y] = 2;
					lastMoveX = x;
					lastMoveY = y;
					System.out.println("Put stone at " + (x+1) + "," + (y+1));
				} catch(NumberFormatException e) {
					continue;
				} catch(ArrayIndexOutOfBoundsException e) {
					continue;
				}
				if(input.length > 2 && input[2].equalsIgnoreCase("c")) {
					gettingCaptureList = true;
				} else {
					isValid = true;
				}
			} else {
				String[] input = new String[] {""};
				while(!input[0].equalsIgnoreCase("done")) {
					System.out.println("Enter location of captured stone or \"done\"");
					System.out.print(">");
					input = IN.readLine().split(" ");
					if(input.length < 2) continue;
					try {
						int x = Integer.parseInt(input[0])-1;
						int y = Integer.parseInt(input[1])-1;
						board[x][y] = 0;
						System.out.println("Stone at " + (x+1) + "," + (y+1) + " removed");
					} catch(NumberFormatException e) {
						continue;
					} catch(ArrayIndexOutOfBoundsException e) {
						continue;
					}
				}
				isValid = true;
			}
		}
	}

}
