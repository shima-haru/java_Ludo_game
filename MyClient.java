//文字化けするためコンパイル時にエンコードしてください
//javac -encoding utf-8 MyClient.java

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.ArrayList;
import java.awt.image.*;//画像処理に必要
import java.awt.geom.*;//画像処理に必要

import java.io.File;//音楽再生時に必要
import javax.sound.sampled.AudioFormat;//音楽再生時に必要
import javax.sound.sampled.AudioSystem;//音楽再生時に必要
import javax.sound.sampled.Clip;//音楽再生時に必要
import javax.sound.sampled.DataLine;//音楽再生時に必要

import java.util.Timer;
import java.util.TimerTask;

public class MyClient extends JFrame implements MouseListener, MouseMotionListener {
	private JLayeredPane c;
	private JButton coma[][], pass, dice; // コマの管理
	private int myTurn; // ターンの管理
	private int myColor; // red0, blue1
	private int DiceNum; // ダイスの数字を覚える
	private JLabel courseLab[], startgoalLab[][]; // マスのラベル redstart bluestart readgoal bluegoal
	private ImageIcon whiteIcon, redIcon, blueIcon;// 盤面image
	private ImageIcon redcoma, bluecoma, redcoma2, bluecoma2;// コマimage
	private int[][] courseplace, redstart, bluestart, redgoal, bluegoal; // マスの座標情報を管理する
	private int[][] redcourseplace, bluecourseplace;// コースマスの管理
	private ImageIcon myIcon, yourIcon, pass1, dice1;// I, you;// 対戦を管理するimage
	private int[][] RBcomaplace;
	private int diceflag, dicemax, turnnum;// サイコロが6が出た場合に必要になるflag
	private int f_step;// 0踏まない、1赤が青を踏む、２青が赤を踏む
	private int comagoal[], PassCount[];// 自分のコマがいくつgoalしたかを管理する。
	private JLabel DiceLab, TurnLab, ComLab, ComaLab;
	private JLabel login, win, lose, playway;
	private JButton back, send, play, Moff, Mon;// ゲームに直接関係のないボタン
	private ImageIcon d1, d2, d3, d4, d5, d6, diceq, diceB, back1, send1, play1, Moff1, Mon1;
	private ImageIcon I_login, I_win, I_lose, I_playway;
	private JTextField T1, T2;// テキスト表示
	private SoundPlayer m_dice, m_walk, m_lose, m_win, m_start, m_BGM, m_step;
	private boolean f_Music;// 音のオンオフのflag

	PrintWriter out;// 出力用のライター

	public MyClient() {

		/*---------------全体初期設定---------------*/
		// BGM、効果音の初期設定
		f_Music = true;// tureの場合は音が出る
		m_BGM = new SoundPlayer("BGM.wav");
		m_BGM.SetLoop(true);// ＢＧＭとして再生を繰り返す
		m_BGM.play();

		c = new JLayeredPane();
		// ウィンドウを作成する
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("ルドーゲーム盤");
		this.setSize(920, 950);
		c.setSize(920, 950);
		c.setLayout(null);
		this.getContentPane().add(c);

		/*---------------ログイン画面---------------*/
		// ログイン画面で使用するもの
		I_login = new ImageIcon("ログイン画面.png");
		login = new JLabel(I_login);
		login.setBounds(0, 0, 900, 900);
		c.add(login);
		c.setLayer(login, 100);

		// 名前を入力するテキストフィールドの作成
		T1 = new JTextField("");
		c.add(T1);
		T1.setBounds(230, 195, 140, 30);
		c.setLayer(T1, 101);

		// IPアドレスを入力するテキストフィールドの作成
		T2 = new JTextField("");
		c.add(T2);
		T2.setBounds(230, 240, 140, 30);
		c.setLayer(T2, 101);

		// 送信ボタン
		send1 = new ImageIcon("開始ボタン.png");
		send = new JButton(send1);
		c.add(send);// ペインに貼り付ける
		send.setBounds(50, 300, 100, 45);
		send.addMouseListener(this);
		send.addMouseMotionListener(this);
		send.setOpaque(false);
		c.setLayer(send, 101);

		/*---------------ゲームの初期設定---------------*/
		// ゲーム中に使うアイコンの設定
		whiteIcon = new ImageIcon("白マス.png");
		redIcon = new ImageIcon("赤マス.png");
		blueIcon = new ImageIcon("青マス.png");
		redcoma = new ImageIcon("コマ赤.png");
		redcoma2 = new ImageIcon("コマ赤変化.png");
		bluecoma = new ImageIcon("コマ青.png");
		bluecoma2 = new ImageIcon("コマ青変化.png");
		pass1 = new ImageIcon("pass");
		dice1 = new ImageIcon("dice");
		d1 = new ImageIcon("1.png");
		d2 = new ImageIcon("2.png");
		d3 = new ImageIcon("3.png");
		d4 = new ImageIcon("4.png");
		d5 = new ImageIcon("5.png");
		d6 = new ImageIcon("6.png");
		diceq = new ImageIcon("Dice.png");
		diceB = new ImageIcon("diceボタン.png");
		back1 = new ImageIcon("戻るボタン.png");
		I_win = new ImageIcon("勝利画面.png");
		I_lose = new ImageIcon("敗北画面.png");
		I_playway = new ImageIcon("遊び方.png");
		play1 = new ImageIcon("遊び方ボタン.png");
		Moff1 = new ImageIcon("音OFFボタン.png");
		Mon1 = new ImageIcon("音ONボタン.png");

		// コースのマスの位置を管理する配列
		courseplace = new int[24][2];
		int[][] courseplace2 = { { 318, 120 }, { 418, 120 }, { 518, 120 }, { 518, 220 }, { 518, 320 }, { 618, 320 },
				{ 718, 320 }, { 718, 420 }, { 718, 520 }, { 618, 520 }, { 518, 520 }, { 518, 620 },
				{ 518, 720 }, { 418, 720 }, { 318, 720 }, { 318, 620 }, { 318, 520 }, { 218, 520 },
				{ 118, 520 }, { 118, 420 }, { 118, 320 }, { 218, 320 }, { 318, 320 }, { 318, 220 },
		};
		courseplace = courseplace2;

		// 赤コマの位置を決める座標
		redcourseplace = new int[28][2];
		int[][] redcourseplace2 = { { 518, 120 }, { 518, 220 }, { 518, 320 }, { 618, 320 },
				{ 718, 320 }, { 718, 420 }, { 718, 520 }, { 618, 520 }, { 518, 520 }, { 518, 620 },
				{ 518, 720 }, { 418, 720 }, { 318, 720 }, { 318, 620 }, { 318, 520 }, { 218, 520 },
				{ 118, 520 }, { 118, 420 }, { 118, 320 }, { 218, 320 }, { 318, 320 }, { 318, 220 }, { 318, 120 },
				{ 418, 120 },
				{ 418, 220 }, { 418, 320 }, { 518, 420 }, { 618, 420 } };/* redgoalの座標 */
		redcourseplace = redcourseplace2;

		// 青コマの位置を決める座標
		bluecourseplace = new int[28][2];
		int[][] bluecourseplace2 = {
				{ 318, 720 }, { 318, 620 }, { 318, 520 }, { 218, 520 }, { 118, 520 }, { 118, 420 },
				{ 118, 320 }, { 218, 320 }, { 318, 320 }, { 318, 220 }, { 318, 120 }, { 418, 120 },
				{ 518, 120 }, { 518, 220 }, { 518, 320 }, { 618, 320 }, { 718, 320 }, { 718, 420 },
				{ 718, 520 }, { 618, 520 }, { 518, 520 }, { 518, 620 }, { 518, 720 }, { 418, 720 },
				{ 418, 620 }, { 418, 520 }, { 318, 420 }, { 218, 420 } };/* bluegoalの座標 */
		bluecourseplace = bluecourseplace2;

		// 赤コマと青コマの位置を番号で管理する、ここから座標を呼び出す
		RBcomaplace = new int[2][4];// [0][]red, [1][]blue
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 4; i++) {
				RBcomaplace[j][i] = -1;
			}
		}

		// 赤のスタート
		redstart = new int[4][2];
		int[][] redstart2 = { { 655, 80 }, { 755, 80 }, { 655, 180 }, { 755, 180 } };
		redstart = redstart2;

		// 青のスタート
		bluestart = new int[4][2];
		int[][] bluestart2 = { { 85, 655 }, { 185, 655 }, { 85, 755 }, { 185, 755 } };
		bluestart = bluestart2;

		// 赤のゴール
		redgoal = new int[4][2];
		int[][] redgoal2 = { { 418, 220 }, { 418, 320 }, { 518, 420 }, { 618, 420 } };
		redgoal = redgoal2;

		// 青のゴール
		bluegoal = new int[4][2];
		int[][] bluegoal2 = { { 218, 420 }, { 318, 420 }, { 418, 520 }, { 418, 620 } };
		bluegoal = bluegoal2;

		// コマを初期位置に配置する
		coma = new JButton[2][4];// [0][]red, [1][]blue
		int k = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				if (i == 0) {
					coma[i][j] = new JButton(redcoma);
					c.add(coma[i][j], 0);
					coma[i][j].setBounds(redstart[j][0], redstart[j][1] - 30, 65, 99);// ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
					coma[i][j].setOpaque(false);
					coma[i][j].setContentAreaFilled(false);
					coma[i][j].setBorderPainted(false);
				} else if (i == 1) {
					coma[i][j] = new JButton(bluecoma);
					c.add(coma[i][j], 1);
					coma[i][j].setBounds(bluestart[j][0], bluestart[j][1] - 30, 65, 99);// ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
					coma[i][j].setOpaque(false);
					coma[i][j].setContentAreaFilled(false);
					coma[i][j].setBorderPainted(false);
				}
				coma[i][j].addMouseListener(this);// ボタンをマウスでさわったときに反応するようにする
				coma[i][j].addMouseMotionListener(this);// ボタンをマウスで動かそうとしたときに反応するようにする
				coma[i][j].setActionCommand(Integer.toString(k));// ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
				k++;
			}
		}

		// 自分のコマがgoalしたかを0,1で管理する。
		comagoal = new int[4];

		// コースマスの設定
		courseLab = new JLabel[24];
		for (int i = 0; i < 24; i++) {
			if (i == 2) {
				courseLab[i] = new JLabel(redIcon);
			} else if (i == 14) {
				courseLab[i] = new JLabel(blueIcon);
			} else {
				courseLab[i] = new JLabel(whiteIcon);
			}
			c.add(courseLab[i], -1);
			courseLab[i].setBounds(courseplace[i][0], courseplace[i][1], 65, 65);
		}

		// ダイスを振った回数を管理するためのもの
		// ダイスの６が出た場合にもう一度さいころを振れるため
		diceflag = 0;
		dicemax = 1;
		turnnum = 1;

		// autopassの管理
		PassCount = new int[4];

		// スタートゴールのマス配置
		startgoalLab = new JLabel[4][4];
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				if (j == 0) {
					startgoalLab[j][i] = new JLabel(whiteIcon);
					c.add(startgoalLab[j][i], -1);
					startgoalLab[j][i].setBounds(redstart[i][0], redstart[i][1], 65, 65);
				} else if (j == 1) {
					startgoalLab[j][i] = new JLabel(whiteIcon);
					c.add(startgoalLab[j][i], -1);
					startgoalLab[j][i].setBounds(bluestart[i][0], bluestart[i][1], 65, 65);
				} else if (j == 2) {
					startgoalLab[j][i] = new JLabel(redIcon);
					c.add(startgoalLab[j][i], -1);
					startgoalLab[j][i].setBounds(redgoal[i][0], redgoal[i][1], 65, 65);
				} else if (j == 3) {
					startgoalLab[j][i] = new JLabel(blueIcon);
					c.add(startgoalLab[j][i], -1);
					startgoalLab[j][i].setBounds(bluegoal[i][0], bluegoal[i][1], 65, 65);
				}
			}
		}

		/*---------------その他JButton、JLabelの設定---------------*/
		// passボタンの作成
		// デバック時のみ必要でありゲーム中は使わないためc.addはコメントアウトしておく
		pass = new JButton("pass", pass1);
		// c.add(pass, -1);// ペインに貼り付ける
		pass.setBounds(550, 800, 90, 45);
		pass.addMouseListener(this);
		pass.addMouseMotionListener(this);

		// diceボタンの作成
		dice = new JButton(diceB);
		c.add(dice, -1);// ペインに貼り付ける
		dice.setBounds(695, 800, 100, 45);
		dice.addMouseListener(this);
		dice.addMouseMotionListener(this);
		dice.setOpaque(false);

		// 勝利画面
		// ゲーム終了時まで後ろのレイヤーに隠しておく
		win = new JLabel(I_win);
		win.setBounds(150, 250, 600, 400);
		c.add(win);
		c.setLayer(win, -10);

		// 敗北画面
		// ゲーム終了時まで後ろのレイヤーに隠しておく
		lose = new JLabel(I_lose);
		lose.setBounds(150, 250, 600, 400);
		c.add(lose, -10);
		c.setLayer(lose, -10);

		// 遊び方画面
		// ゲーム終了時まで後ろのレイヤーに隠しておく
		playway = new JLabel(I_playway);
		playway.setBounds(50, 130, 800, 533);
		c.add(playway, -10);
		c.setLayer(playway, -10);

		// 戻るボタン
		// 勝利画面と敗北画面を消すためのボタン
		back = new JButton(back1);
		c.add(back);
		back.setBounds(400, 600, 100, 45);
		back.addMouseListener(this);
		back.addMouseMotionListener(this);
		back.setOpaque(false);
		c.setLayer(back, -10);

		// 音声に関するボタン
		// ON
		Mon = new JButton(Mon1);
		c.add(Mon);// ペインに貼り付ける
		Mon.setBounds(600, 50, 100, 45);
		Mon.addMouseListener(this);
		Mon.addMouseMotionListener(this);
		Mon.setOpaque(false);
		c.setLayer(Mon, 101);
		// OFFボタン
		Moff = new JButton(Moff1);
		c.add(Moff);// ペインに貼り付ける
		Moff.setBounds(600, 100, 100, 45);
		Moff.addMouseListener(this);
		Moff.addMouseMotionListener(this);
		Moff.setOpaque(false);
		c.setLayer(Moff, 101);

		// 遊び方ボタン
		play = new JButton(play1);
		c.add(play);// ペインに貼り付ける
		play.setBounds(600, 170, 100, 45);
		play.addMouseListener(this);
		play.addMouseMotionListener(this);
		play.setOpaque(false);
		c.setLayer(play, 101);

		// 書き換えのあるテキストなどを表示する領域
		// ダイスの画像を表示する
		DiceLab = new JLabel(diceq);
		DiceLab.setBounds(670, 640, 150, 150);
		c.add(DiceLab, 1);

		// どちらのターンか表示する
		TurnLab = new JLabel("");
		c.add(TurnLab, 1);
		TurnLab.setBounds(120, 50, 250, 50);
		TurnLab.setFont(new Font("メイリオ", Font.BOLD, 35));

		// コメントがある場合に表示する
		ComLab = new JLabel("");
		ComLab.setText("");
		ComLab.setBounds(40, 140, 300, 50);
		ComLab.setFont(new Font("メイリオ", Font.BOLD, 20));
		ComLab.setForeground(Color.BLACK);
		c.add(ComLab, 1);

		// 背景の盤面
		ImageIcon icon1 = new ImageIcon("盤面のみ.png");
		JLabel label1 = new JLabel(icon1);
		c.add(label1, -2);
		label1.setBounds(0, 0, 900, 900);
	}

	// メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {

		Socket socket;
		String myName;

		public MesgRecvThread(Socket s, String n) {
			socket = s;
			myName = n;
		}

		// 通信状況を監視し，受信データによって動作する
		public void run() {
			try {
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);// 接続の最初に名前を送る

				String myNumberStr = br.readLine();
				System.out.println("プリント" + myNumberStr);
				int myNumberInt = Integer.parseInt(myNumberStr);// 何番目の接続か
				if (myNumberInt % 2 == 0) {// 偶数番目に繋いだ端末が先行
					myColor = 0;
					myIcon = redcoma;
					yourIcon = bluecoma;
					myTurn = 0;
					System.out.println("自分は" + myIcon);
					ComaLab = new JLabel(redcoma);
					ComaLab.setBounds(50, 20, 65, 99);
					c.add(ComaLab, 1);
					TurnLab.setText("あなたのターン");
					TurnLab.setForeground(Color.RED);
				} else {// 奇数番目に繋いだ端末がが後攻
					myColor = 1;
					myIcon = bluecoma;
					yourIcon = redcoma;
					myTurn = 1;
					System.out.println("自分は" + myIcon);
					ComaLab = new JLabel(redcoma);
					ComaLab.setBounds(50, 20, 65, 99);
					c.add(ComaLab, 1);
					TurnLab.setText("相手のターン");
					c.add(TurnLab, 1);
					TurnLab.setForeground(Color.RED);
				}

				while (true) {
					String inputLine = br.readLine();// データを一行分だけ読み込んでみる
					if (inputLine != null) {// 読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);// デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" "); // 入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];// コマンドの取り出し．１つ目の要素を取り出す
						// パス
						if (cmd.equals("PASS")) {// パスをする処理
							String m_passnum = inputTokens[1];// 手動パスは0、オートパスは1 //手動パスはデバック専用
							int passnum = Integer.parseInt(m_passnum);
							if (passnum == 1) {
								ComLab.setText("動かせるコマがありません");
								// 画面切り替えが早すぎるので遅らせる
								try {
									Thread.sleep(2000);
								} catch (InterruptedException ee) {
								} // エラーがでてもなにもしない
							}
							if (myTurn == 0) {
								if ((diceflag < dicemax) && (passnum == 1)) {// 6が出た場合サイコロをもう一度振れる
									String msg = "NOTCHANGE";
									out.println(msg);
									out.flush();
									repaint();
								} else {// 特殊な場合でなければターンを入れ替える
									String msg = "CHANGE";
									out.println(msg);
									out.flush();
									repaint();
									System.out.println("パスをしてターンが入れ替わりました");
								}
							}
						}

						// サイコロでコマを動かす処理
						if (cmd.equals("PLACE")) {
							String m_comanum = inputTokens[1];// コマ番号の取得
							int ComaNum = Integer.parseInt(m_comanum);
							String m_comacolor = inputTokens[2];// 色の取得
							int ComaColor = Integer.parseInt(m_comacolor);
							String m_comaplace = inputTokens[3];// コマの動いた位置
							int ComaPlace = Integer.parseInt(m_comaplace);
							String m_stepnum = inputTokens[4];// 相手のコマを踏んだかどうか
							int stepnum = Integer.parseInt(m_stepnum);
							layerupper(ComaColor);
							// 実際にコマを動かしている処理はここから呼び出す
							if (ComaColor == 0) {
								display_redcoma(ComaNum, ComaPlace);
							} else {
								display_bluecoma(ComaNum, ComaPlace);
							}
							// System.out.println("コマの移動完了step処理今から");
							if (myTurn == 0) {// そのターンを行った人だけにやってほしい処理
								if (f_step > 0) {// 相手のコマを踏む場合の処理
									step(f_step, stepnum);
								}
								// System.out.println("step処理完了、勝利判定確認");
								if (judwin(comagoal)) {
									// この時点で勝利判定がつくか見る
									try {// step処理が追い付かないためここでsleepさせる
										Thread.sleep(1000);
									} catch (InterruptedException ee) {
									}
									String msg = "JUDGE" + " " + myColor;
									out.println(msg);
									out.flush();
									repaint();
								} else if (diceflag < dicemax) {
									String msg = "NOTCHANGE";
									out.println(msg);
									out.flush();
									repaint();
								} else {// 特殊な場合でなければターンを入れ替える
									String msg = "CHANGE";
									out.println(msg);
									out.flush();
									repaint();
								}
							}
						}

						// 踏まれたときにコマをスタートに戻す
						if (cmd.equals("STEP")) {
							String m_comanum = inputTokens[1];// 踏まれたコマ番号の取得
							int ComaNum = Integer.parseInt(m_comanum);
							String m_comacolor = inputTokens[2];// 色の取得
							int ComaColor = Integer.parseInt(m_comacolor);
							if (ComaColor == 0) {// 赤のターンなので青が戻る
								RBcomaplace[1][ComaNum] = -1;
								coma[1][ComaNum].setBounds(bluestart[ComaNum][0], bluestart[ComaNum][1] - 30, 65, 99);
							} else {// 青のターンなので赤が戻る
								RBcomaplace[0][ComaNum] = -1;
								coma[0][ComaNum].setBounds(redstart[ComaNum][0], redstart[ComaNum][1] - 30, 65, 99);
							}
							if (f_Music) {
								m_step = new SoundPlayer("踏む.wav");
								m_step.play();
							}
							System.out.println(ComaNum + "は踏まれたのでスタート位置に戻されました");
						}
						if (cmd.equals("JUDGE")) {// 勝敗がついた場合の処理
							String m_comacolor = inputTokens[1];// 色の取得
							int ComaColor = Integer.parseInt(m_comacolor);
							winlose(ComaColor);
							myTurn = 1;// どちらもコマを触れなくする。
						}

						// ターンが入れ替わるときの処理
						if (cmd.equals("CHANGE")) {
							changeturn();
						}

						// ターンが入れ替わらない時の処理（サイコロで6が出た場合）
						if (cmd.equals("NOTCHANGE")) {
							turnnum++;
							f_step = 0;
							PassJudge();
							ComLab.setText("もう一度サイコロを振れます");
						}
						// ダイスの数を画面に表示させる
						if (cmd.equals("DICE")) {
							String m_dicenum = inputTokens[1];// ダイスの取得
							int _dicenum = Integer.parseInt(m_dicenum);
							dicedraw(_dicenum);
							if (myTurn == 0) {
								autopass();
							}
						}
					} else {
						break;
					}
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {// ボタンをクリックしたときの処理
		// ゲームの前後に使うボタン、ゲーム中自由に使えるボタン
		JButton theButton = (JButton) e.getComponent();// クリックしたオブジェクトを得る．型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();// ボタンの配列の番号を取り出す
		Icon theIcon = theButton.getIcon();// theIconには，現在のボタンに設定されたアイコンが入る
		if (theIcon == back1) {// 戻るボタン
			back();
		} else if (theIcon == send1) {// 送信ボタン
			send();
		} else if (theIcon == Mon1) {// 音ON
			M_Start();
			System.out.println("BGM再開");
		} else if (theIcon == Moff1) {// 音OFF
			M_Stop();
		} else if (theIcon == play1) {// 遊び方の画面
			playway();
		}

		// ゲーム中の処理
		else if (myTurn == 0) {// 自分のターン時に使えるボタン
			// デバック時に使用するパスボタンの処理
			if (theIcon == pass1) {
				String msg = "PASS" + " " + 0;
				out.println(msg);
				out.flush();
				repaint();
			}

			// ダイスを振る
			if (theIcon == diceB) {
				// このときダイスを振れるか判定する（2回振れないようにしている）
				if ((dicemax - 1 == diceflag) && (turnnum - 1 == diceflag)) {
					dice();
					String msg = "DICE" + " " + DiceNum;
					out.println(msg);
					out.flush();
					repaint();
				} else {
					System.out.println("これ以上ダイスは振れません");
				}
			}

			// ダイスが振られている場合に動かす自分のコマを選択できるようになる
			if (turnnum == diceflag) {
				if (checkIcon(myColor, theIcon)) {
					int place = -1;
					int temp = Integer.parseInt(theArrayIndex);
					int x = temp / 4;
					int y = temp % 4;
					System.out.println("選択した色は：" + x + " ,その中のコマ番号は：" + y);

					if (Boolmove(y)) {// クリックしたコマが動かせるか判定を行う
						place = move(y);// コマを動かした場合の位置をここで記憶しておく
						int _step = Boolstep(place);
						if (_step != 100) {// 青を踏んだ場合flagを立てておく
							if (myColor == 0) {
								f_step = 1;
							} else {
								f_step = 2;
							}
						}
						String msg = "PLACE" + " " + y + " " + myColor + " " + place + " " + _step;
						out.println(msg);
						out.flush();
					} else {// 動かせない場合
						System.out.println("別のコマを選択してください");
					}
				} else if (theIcon == yourIcon) {// 相手のアイコンはクリックできない
					// System.out.println("自分の色じゃありません");
				}
			} else {
				System.out.println("先にダイスを振ってください");
			}
			changecoma(theButton);// 色が変わってしまっているアイコンをもとの色に戻す
			repaint();
		}
	}

	// マウスが入ったらコマの色を変える
	// サイコロを振って動かせるコマのみ色を変えられる
	public void mouseEntered(MouseEvent e) {
		if ((myTurn == 0) && (turnnum == diceflag)) {
			JButton theButton1 = (JButton) e.getComponent();
			Icon theIcon = theButton1.getIcon();
			if (theIcon == myIcon) {
				String theArrayIndex1 = theButton1.getActionCommand();
				int temp1 = Integer.parseInt(theArrayIndex1);
				int x = temp1 / 4;
				int y = temp1 % 4;
				if (PassCount[y] == 0) {// 動かせないコマは色が変わらない
					if (x == 0) {
						coma[x][y].setIcon(redcoma2);
					} else if (x == 1) {
						coma[x][y].setIcon(bluecoma2);
					}
				}
			}
		}

	}

	// マウスが出たらコマの色を戻す
	public void mouseExited(MouseEvent e) {
		JButton theButton2 = (JButton) e.getComponent();// クリックしたオブジェクトを得る．型が違うのでキャストする
		changecoma(theButton2);
	}

	// その他のマウスの処理は使わない
	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	/*---------------ゲーム中にのコマの移動に関する関数---------------*/
	// ダイスを振ったときの関数
	public int dice() {
		diceflag++;
		Random random = new Random();
		DiceNum = 1 + random.nextInt(6);
		System.out.println("出たさいころの目は: " + DiceNum);
		if (DiceNum == 6) {
			dicemax++;
		}

		return DiceNum;// 振ったサイコロの目を返してサーバーに送る
	}

	// redblue
	// redコマが移動できるかの判定を行う
	// autopassの判定と自分のターン時にコマをクリックしたときに呼び出される
	public boolean Boolmove(int comanum) {
		if (DiceNum % 2 == 1) {
			if (RBcomaplace[myColor][comanum] == -1) {
				// System.out.println("diceが偶数の時にstartできます。今回は" + DiceNum +
				// "のためこのコマはスタートから出せません");
				return false;
			}
		}
		int ifplace = Ifmove(comanum); // moveのようかコマを動かした場合にどうなるかの処理が必要
		// System.out.println("仮の移動位置" + ifplace);
		for (int i = 0; i < 4; i++) {
			if (comanum != i) {
				if ((ifplace == RBcomaplace[myColor][i]) && (RBcomaplace[myColor][i] != -1)) {
					// System.out.println("移動先に自分の" + i + "のコマがあるためこのコマは移動できません");
					return false;
				} else if (RBcomaplace[myColor][comanum] == 27) {
					// System.out.println("このコマは既にゴールしています");
					return false;
				} else if (ifplace > 27) {
					// System.out.println("goalを超えてしまうため動かせません");
					return false;
				} else {
					// System.out.println(i + "のコマと衝突なし");
				}
			}
		}
		return true;
	}

	// 各コマが移動可能かここで判定する
	public int Ifmove(int comanum) {
		int ifplace = RBcomaplace[myColor][comanum];

		if (ifplace == -1) {
			ifplace = 0;
			return ifplace;
		}
		// 変数書き換える
		for (int i = 0; i < DiceNum; i++) {
			if (ifplace == -1) {// 初期位置はマイナスとして管理する
				ifplace = 0;
				// System.out.println("ifstart");
			} else {
				ifplace++;
				// System.out.println("ifgo");
			}
		}
		return ifplace;
	}

	// 移動したとき相手のアイコンを踏むかの判定を行う
	// もし衝突した場合は後で処理を行う
	public int Boolstep(int place) {
		if (place < 24) {
			place += 12;
			place %= 24;
			for (int i = 0; i < 4; i++) {
				if (RBcomaplace[1 - myColor][i] == place) {// コマの移動先に相手のコマがある
					return i;// 衝突する青のコマ番号を返す
				}
			}
			// System.out.println("相手のコマと衝突しません");
		}
		return 100; // どのコマとも衝突しなかった場合
	}

	// クライアント側で事前にコマを動かしたときの場所を記憶し、goalしているコマを管理する
	public int move(int comanum) {
		// System.out.println("コマ番号: " + comanum + " 動かす前の場所: " +
		// RBcomaplace[myColor][comanum]);
		int nowplace = RBcomaplace[myColor][comanum];
		if (nowplace == -1) {// 初期位置はマイナスとして管理する
			nowplace = 0;
			// System.out.println("start");
			// System.out.println(comanum + "はコースにでました");
			return nowplace;
		}

		for (int i = 0; i < DiceNum; i++) {

			if (nowplace == 27) {// それ以上動かないようにする
				System.out.println("このコマはこれ以上動きません");
			} else {
				nowplace++;
			}
		}
		if (nowplace > 23) {// コマがgoalする場合はここでgoalflagを立てる
			comagoal[comanum] = 1;
		}
		// System.out.println("コマ番号: " + comanum + " 場所: " + nowplace);
		return nowplace; // クリックしたコマの移動先を返している
	}

	/*---------------実際にコマを動かしている処理---------------*/
	// 実際の座標を参照したりするためredとblueで処理を分ける

	// 赤コマを実際に動かしている関数
	public void display_redcoma(int redcomanum, int ComaPlace) {

		int nowplace = RBcomaplace[0][redcomanum];
		for (int i = nowplace + 1; i <= ComaPlace; i++) {
			coma[0][redcomanum].setBounds(redcourseplace[i][0], redcourseplace[i][1] - 30, 65, 99);// 次のcourse[]の位置に配置する
			if (f_Music) {
				m_walk = new SoundPlayer("歩く.wav");
				m_walk.play();
			}
			c.repaint();
			c.paintImmediately(c.getBounds());
			try {
				Thread.sleep(500);
			} catch (InterruptedException ee) {
			}
		}
		RBcomaplace[0][redcomanum] = ComaPlace;
	}

	// 青コマを実際に動かしている関数
	public void display_bluecoma(int bluecomanum, int ComaPlace) {

		int nowplace = RBcomaplace[1][bluecomanum];
		for (int i = nowplace + 1; i <= ComaPlace; i++) {
			coma[1][bluecomanum].setBounds(bluecourseplace[i][0], bluecourseplace[i][1] - 30, 65, 99);// 次のcourse[]の位置に配置する
			if (f_Music) {
				m_walk = new SoundPlayer("歩く.wav");
				m_walk.play();
			}
			c.repaint();
			c.paintImmediately(c.getBounds());
			try {
				Thread.sleep(500);
			} catch (InterruptedException ee) {
			}
		}
		RBcomaplace[1][bluecomanum] = ComaPlace;
	}

	/*-----------------ゲーム中その他関数-----------------*/

	// クリックしたアイコンが何であるか判定する
	public boolean checkIcon(int _theColor, Icon _theIcon) {
		if (_theColor == 0) {
			if (_theIcon == redcoma2) {
				return true;
			}
		} else if (_theColor == 1) {
			if (_theIcon == bluecoma2) {
				return true;
			}
		}
		return false;
	}

	// 移動中のコマのレイヤーが一番上になるようにplace処理を行うたびに相手のレイヤーよりレイヤーを１つずつ上げる
	public void layerupper(int color) {
		int undercolor = 1 - color;
		int lay = c.getLayer(coma[undercolor][0]);
		// System.out.println("相手lay: " + lay);
		for (int i = 0; i < 4; i++) {
			c.setLayer(coma[color][i], lay + 1);
		}
		c.repaint();
		c.paintImmediately(c.getBounds());
		// System.out.println("自分の再描画lay: " + c.getLayer(coma[color][0]));
	}

	// ターン終了時にmouseEnteredでコマの色が変化したままになっているため元に戻す
	public void changecoma(JButton theButton2) {
		if (myTurn == 0) {

			Icon theIcon = theButton2.getIcon();// theIconには，現在のボタンに設定されたアイコンが入る
			// System.out.println("マウス出た");

			if (checkIcon(myColor, theIcon)) {
				String theArrayIndex2 = theButton2.getActionCommand();// ボタンの配列の番号を取り出す
				int temp2 = Integer.parseInt(theArrayIndex2);
				int x = temp2 / 4;
				int y = temp2 % 4;
				// System.out.println("出た色は：" + x + " ,その中のコマ番号は：" + y);
				if (x == 0) {
					coma[x][y].setIcon(redcoma);
				} else if (x == 1) {
					coma[x][y].setIcon(bluecoma);
				}
			}
		}
	}

	// コマを動かしたときに相手のコマを踏んでいた場合はここから踏まれたコマをスタートに戻す処理を行う
	public void step(int f_step, int stepnum) {
		if (f_step == 1) {
			System.out.println("赤のコマが青の" + stepnum + "のコマを踏みました");
			String msg = "STEP" + " " + stepnum + " " + myColor;
			out.println(msg);
			out.flush();
		} else if (f_step == 2) {
			System.out.println("青のコマが赤の" + stepnum + "のコマを踏みました");
			String msg = "STEP" + " " + stepnum + " " + myColor;
			out.println(msg);
			out.flush();
		}
		c.repaint();
		c.paintImmediately(c.getBounds());
	}

	// ターンが入れ替わるタイミングで４つのコマがゴールしているか判定する
	// すべてのコマがゴールしている場合はこの後自動勝利判定へと移る
	public boolean judwin(int[] comagoal) {
		int goalnum = 0;
		for (int i = 0; i < 4; i++) {
			if (comagoal[i] > 0) {
				goalnum++;
			}
		}
		System.out.println("goalしているコマの数は: " + goalnum);
		if (goalnum == 4) {
			return true;
		}
		return false;
	}

	// ターン入れ替え時に必要な処理をすべてここから呼び出す
	public void changeturn() {
		// ターンを入れ替えるための処理をここで行う
		myTurn = 1 - myTurn;
		turnnum = 1;
		diceflag = 0;
		dicemax = 1;
		f_step = 0;// 毎ターン相手を踏んでいるかのflagを下げる
		turntext();
		DiceLab.setIcon(diceq);
		ComLab.setText("");
		PassJudge();
		System.out.println("ターンが入れ替わりました");
	}

	// サイコロを振った時点で自動パスが発生するかを判定する
	public void autopass() {
		int autopass = 0;
		for (int i = 0; i < 4; i++) {
			// System.out.println("判定するコマ番号は" + i);
			if (myColor == 0) {
				if (!Boolmove(i)) {
					autopass++;
					PassCount[i] = 1;
				}
			} else if (myColor == 1) {
				if (!Boolmove(i)) {
					autopass++;
					PassCount[i] = 1;
				}
			}
		}
		System.out.println("動かせるコマの数は" + (4 - autopass));
		if (autopass == 4) {
			System.out.println("動かせるコマがないためパスします。");
			String msg = "PASS" + " " + 1;// 自動パス発生時にはPASSする処理をserverに送る
			out.println(msg);
			out.flush();
			repaint();

		} else {
			// System.out.println("自分のターンに動かせるコマがあります");
		}
	}

	// ダイスの数字を画面に表示する
	public void dicedraw(int dice) {
		if (f_Music) {
			m_dice = new SoundPlayer("ダイス.wav");
			m_dice.play();
		}
		switch (dice) {
			case 1:
				DiceLab.setIcon(d1);
				break;
			case 2:
				DiceLab.setIcon(d2);
				break;
			case 3:
				DiceLab.setIcon(d3);
				break;
			case 4:
				DiceLab.setIcon(d4);
				break;
			case 5:
				DiceLab.setIcon(d5);
				break;
			case 6:
				DiceLab.setIcon(d6);
				break;
			default:
		}
		c.repaint();
		c.paintImmediately(c.getBounds());
	}

	// 動かせないコマを管理する配列を初期化する
	public void PassJudge() {
		for (int i = 0; i < 4; i++) {
			PassCount[i] = 0;
		}
	}

	// ターン入れ替わり時に画面に表示するテキストを変更する関数
	public void turntext() {
		if (myTurn == 0) {
			TurnLab.setText("あなたのターン");
			if (myColor == 0) {
				TurnLab.setForeground(Color.RED);
				ComaLab.setIcon(redcoma);
			} else {
				TurnLab.setForeground(Color.BLUE);
				ComaLab.setIcon(bluecoma);
			}
		} else {
			TurnLab.setText("相手のターン");
			if (myColor == 1) {
				TurnLab.setForeground(Color.RED);
				ComaLab.setIcon(redcoma);
			} else {
				TurnLab.setForeground(Color.BLUE);
				ComaLab.setIcon(bluecoma);
			}
		}
		c.repaint();
		c.paintImmediately(c.getBounds());
	}

	/*---------------汎用使用のボタンに関する関数---------------*/
	// 遊び方関数
	public void playway() {
		c.setLayer(playway, 200);
		c.setLayer(back, 201);
	}

	// 戻るボタンの処理
	public void back() {
		c.setLayer(win, -10);
		c.setLayer(lose, -10);
		c.setLayer(back, -10);
		c.setLayer(playway, -10);
	}

	/*---------------勝敗後に必要な関数---------------*/
	// 勝敗が決まったときに呼び出される関数
	public void winlose(int winColor) {
		ComLab.setText("");

		if (winColor == myColor) {// 勝った方の画面にする処理
			System.out.println("勝利");
			TurnLab.setText("勝利");
			c.setLayer(win, 1000);
			if (f_Music) {
				m_win = new SoundPlayer("勝ち.wav");
				m_win.play();
			}
		} else {// 負けた方の画面にする処理
			System.out.println("敗北");
			TurnLab.setText("敗北");
			c.setLayer(lose, 1000);
			if (f_Music) {
				m_lose = new SoundPlayer("負け.wav");
				m_lose.play();
			}
		}
		c.setLayer(back, 1001);
		TurnLab.setForeground(Color.BLACK);

		if (myColor == 0) {
			ComaLab.setIcon(redcoma);
		} else {
			ComaLab.setIcon(bluecoma);
		}
	}

	/*---------------ログイン時に必要な関数---------------*/
	// ゲームの前に名前とIPアドレスを送る関数
	public void send() {
		// start時の効果音を流す
		if (f_Music) {
			m_start = new SoundPlayer("開始.wav");
			m_start.play();
		}

		String myName = T1.getText();
		System.out.println("myName: " + myName);
		if ((myName.equals("")) || (myName.equals("名前を入力"))) {
			myName = "No name";
		}

		String IPname = T2.getText();
		System.out.println("IPname: " + IPname);
		socket(myName, IPname);

		// ログイン画面で使っていたボタンでゲーム画面でも使うボタンの位置がわかるためセットしなおす
		c.setLayer(Mon, 1);
		Mon.setBounds(310, 800, 100, 45);
		c.setLayer(Moff, 1);
		Moff.setBounds(415, 800, 100, 45);
		c.setLayer(play, 1);
		play.setBounds(520, 800, 100, 45);

		// ログイン画面で使っていたもののレイヤーを後ろにする
		c.setLayer(login, -100);
		c.setLayer(T1, -101);
		c.setLayer(T2, -101);
		c.setLayer(send, -101);

	}

	// IPアドレスを打ち込んでセーバーと通信する際に使う関数
	public void socket(String myName, String IPname) {
		// サーバに接続する
		Socket socket = null;
		try {
			if ((IPname.equals("")) || (IPname.equals("IPアドレスを入力"))) {
				socket = new Socket("localhost", 10000);
			} else {
				socket = new Socket(IPname, 10000);
			}

		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			System.err.println("エラーが発生しました: " + e);
		}
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);// 受信用のスレッドを作成する
		mrt.start();// スレッドを動かす（Runが動く）
	}

	/*---------------音関係の関数---------------*/
	// BGM、効果音をOFFにする場合の関数
	public void M_Stop() {
		f_Music = false;
		m_BGM.stop();
	}

	// BGM、効果音をONにする場合の関数
	public void M_Start() {
		System.out.println("start処理中");
		f_Music = true;
		m_BGM.SetLoop(true);// ＢＧＭとして再生を繰り返す
		m_BGM.play();
	}

	// 音楽再生関係の関数
	public class SoundPlayer {
		private AudioFormat format = null;
		private DataLine.Info info = null;
		private Clip clip = null;
		boolean stopFlag = false;
		Thread soundThread = null;
		private boolean loopFlag = false;

		public SoundPlayer(String pathname) {
			File file = new File(pathname);
			try {
				format = AudioSystem.getAudioFileFormat(file).getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(AudioSystem.getAudioInputStream(file));
				// clip.setLoopPoints(0,clip.getFrameLength());//無限ループとなる
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void SetLoop(boolean flag) {
			loopFlag = flag;
		}

		public void play() {
			stopFlag = false;
			soundThread = new Thread() {
				public void run() {
					long time = (long) clip.getFrameLength();// 44100で割ると再生時間（秒）がでる
					// System.out.println("PlaySound time="+time);
					long endTime = System.currentTimeMillis() + time * 1000 / 44100;
					clip.start();
					// System.out.println("PlaySound time="+(int)(time/44100));
					while (true) {
						if (stopFlag) {// stopFlagがtrueになった終了
							// System.out.println("PlaySound stop by stopFlag");
							clip.stop();
							return;
						}
						// System.out.println("endTime="+endTime);
						// System.out.println("currentTimeMillis="+System.currentTimeMillis());
						if (endTime < System.currentTimeMillis()) {// 曲の長さを過ぎたら終了
							// System.out.println("PlaySound stop by sound length");
							if (loopFlag) {
								clip.loop(1);// 無限ループとなる
							} else {
								clip.stop();
								return;
							}
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			soundThread.start();
		}

		public void stop() {
			stopFlag = true;
			System.out.println("StopSound");
		}

	}
}