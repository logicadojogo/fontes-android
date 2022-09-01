package br.com.mvbos.lgj.tetris;


import android.graphics.Color;

public class Peca {
	public static int[] Cores = { Color.GREEN, Color.DKGRAY, Color.YELLOW, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.WHITE };

	public static final int[][][] PECAS = {
			{
				{ 0, 1, 0 },
				{ 0, 1, 0 },
				{ 1, 1, 0 } },
			{
				{ 0, 1, 0 },
				{ 0, 1, 0 },
				{ 0, 1, 1 } },
			{
				{ 1, 1, 1 },
				{ 0, 1, 0 },
				{ 0, 0, 0 } },
			{
				{ 1, 0, 0 },
				{ 1, 1, 0 },
				{ 0, 1, 0 } },
			{
				{ 0, 0, 1 },
				{ 0, 1, 1 },
				{ 0, 1, 0 } },
			{
				{ 1, 1 },
				{ 1, 1 } },
			{
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 } }
	};

}
