package def;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.FilerException;

public class Main {

	public static void main(String[] args) throws FilerException {
		if(args.length == 0) {
			System.out.println("データ入れて\n");
			System.exit(0);
		}
		File bitsfile = new File(args[0]);
		FileReader fr = null;
		BufferedReader bf = null;

		int bitNum = 0;
		String onedata = new String();

		List<String> bitsList = new ArrayList<>();
		int oneNum;
		List<ProcessBits> bitsSortList = new ArrayList<>();
		List<ProcessBits> dealt_front_List = new ArrayList<>();

		int prerow = 0;
		int nowrow = 0;

		try {
			fr = new FileReader(bitsfile);
			bf = new BufferedReader(fr);

			while((onedata = bf.readLine()) != null) {
				bitsList.add(onedata);
			}

			bitNum = bitsList.get(0).length();

			for(int i=0;i<=bitNum;i++) {

				for(String temp:bitsList) {
					oneNum=0;

					if(!bitsSortList.contains(new ProcessBits(temp))) {
						for(int j = 0;j<temp.length();j++) {
							if(temp.charAt(j) == '1') {
								oneNum++;
							}
						}

						if(oneNum == i) {
							bitsSortList.add(new ProcessBits(temp));
						}
					}
				}
			}

			System.out.println("---SORTED BY NUMBER OF ONE---");
			for(ProcessBits temp:bitsSortList) {
				System.out.println(temp.getBits());
			}

			int count=0;
			int bitelements=0;
			boolean elementflag = false;

			//ここからまとめていく
			/*
			 * 実は本当と少し違う
			 * --繰り返し--
			 * ビット情報（文字列）の個数を知る
			 * --countが情報個数に追いつくまで繰り返し--
			 * ベースとするビット情報をbitsSortListの配列番号countで取得
			 * まとめられるかそれぞれ試す対象をbitsSortList[count+1]以降で取得
			 * 条件に合うならまとめたビット情報をbitsSortListに入れる
			 * -------------------------------
			 * countが情報個数を追い越した
			 *   = 同まとめ段階はまとめ終えた
			 * まとめた印がついてるビット情報はリストから消去する
			 *
			 * --まとめレベルの最大値が上がらなければ処理終了
			 */
			while(true) {	//bitsSortListの中身を更新しながら進むので、拡張for文は使えない
				if(elementflag == false) {
					bitelements = bitsSortList.size();
					elementflag = true;
				}

				ProcessBits base = bitsSortList.get(count);

				for(int i = count+1;i < bitelements ;i++) {
					//debug111++;
					ProcessBits target = bitsSortList.get(i);

					//同じ段階同士をまとめようとしてるよね？
					if(base.rowCheck() == target.rowCheck()) {

						//もし、違いが一つなら
						if(base.DifOne(target.getBits())) {

							/*
							 * 新しい文字列を作って
							 * まとめたことを表すチェックを付けて
							 * （チェック付きは主項表に参加させない）
							 * 段階数を増やし、
							 * 新しくbit情報を作る
							 * ->replaceDifになった。
							 * まとめが終了していないことを知らせる
							 */
							ProcessBits newone = base.replaceDif(target);
							bitsSortList.add(newone);

							nowrow++;
						}
					}
				}
				count++;

				if(count >= bitelements) {

					elementflag = false;	//要素数を検知するかを判断する

					//チェック付き判定
					int tmpcount=0;
					while(tmpcount < count) {
						if(bitsSortList.get(tmpcount).getcheck()) {
							dealt_front_List.add(bitsSortList.get(tmpcount));
						}
						tmpcount++;
					}
					//削除
					for(ProcessBits remEle:dealt_front_List) {
						bitsSortList.remove(remEle);
					}

					//次ループ向け処理
					count = 0;
					if(prerow == nowrow) {
						break;
					}else {
						prerow = nowrow;
					}
				}

			}

			//このまとめ方では、同じまとめ情報が生まれるので、揃える。
			//それと今後のためにまとめ段階が高いものを先頭に持っていきたい
			List<ProcessBits> LastbitsList = new ArrayList<>();
			int index = 0; //この配列の何番目に入れるのか
			int min = bitsSortList.get(0).rowCheck(); //とりあえず一番始めに入れる要素の段階数を最小値とする。

			boolean canadd=true; //いま拾ってるのは入れるの？
			for(ProcessBits temp : bitsSortList) {

				canadd = true;
				for(ProcessBits adding : LastbitsList) {

					if(temp.equals(adding)) {
						canadd = false;	//ダブりあったら消すよね。
					}

				}
				if(canadd) {
					if(temp.rowCheck() <= min) {//段階がminと同じかそれ以下なら、一番うしろに入れれば良い
						LastbitsList.add(temp);

						//最小値を変える。
						min = temp.rowCheck();
					}else {//違ったら
						index = 0;
						for(ProcessBits rowmore : LastbitsList) { //上から順番に段階を調べる
							if(rowmore.rowCheck() < temp.rowCheck()) {//もし、tempがrowmoreより大きいなら
								LastbitsList.add(index, temp);
								break;
							}
							index++;
						}
					}

				}
			}

			System.out.println("---SUMMARY RESULT---");
			for(ProcessBits temp:LastbitsList) {
				System.out.println(temp.toString()+"\n");
			}

			//ここから主項表
			/*
			 * 1ProcesBits:1セットから始め、2PB:1セット、3PB:1セットとしていく
			 * 配列の頭番号を保持し、頭番号=取ろうとした番号のときはcontinueしてセットPB数を増やす。
			 * セットPB数がビット情報リスト要素数を越えたら駄目なので、（その前に理論上は止まるが。）
			 * 停止させる。
			 */

			Set<MainClause> ans = new HashSet<>();
			MainClause one = null;
			boolean canbreak = false;	//ansが一つでもあればtrueにして検索フェーズを抜ける
			int head = 0;
			int elenum = LastbitsList.size();
			int adindex; //追加回数カウンタ
			int frHeadtoaddMargen;
			boolean dob = false; //ダブってるかどうか

			//セット数
			for(int i = 0 ; i < elenum ; i++) { //iは頭以外追加数とも読める

				//頭の場所 headBitは頭のインスタンス
				//このループ内で全部が頭になる
				for(head = 0 ; head < elenum ; head++) { //head 頭インデックスとして利用

					//追加組み合わせ管理
					//ここで頭以外全組み合わせがテストされる
					frHeadtoaddMargen = 0;
					//追加の頭までの距離
					while(!((frHeadtoaddMargen != 0) && ((head + frHeadtoaddMargen + i)%elenum == head))) {
						one = new MainClause();
						//頭追加
						one.addBit(LastbitsList.get(head));

						//一通り作る
						adindex = 0;
						while(adindex < i) {//必要数追加できる
							one.addBit(LastbitsList.get((head + frHeadtoaddMargen + adindex + 1) % elenum));
							//リスト最後尾まで言ったあと、リスト先頭に戻ってくるため余り%を使う
							adindex++;
						}
						if(one.correctAllCoverBit(bitsList)) {
							for(MainClause temp : ans) {
								if(one.equals(temp)) {
									dob = true;
								}
							}
							if(!dob) {
								ans.add(one);
							}
							dob = false;
							canbreak = true;
						}
						frHeadtoaddMargen++;
					}
				}
				if(canbreak) {
					break;
				}
			}
			System.out.println("----ANSWERS----\n");
			for(MainClause ones : ans) {
				System.out.println(ones.getBits() + "\nor\n");
			}

		}catch(IOException e) {
			System.out.println(e);
		}

		try {
			fr.close();
		}catch(FilerException e) {
			System.out.println(e);
		}catch(IOException e) {
			System.out.println(e);
		}

	}

}
