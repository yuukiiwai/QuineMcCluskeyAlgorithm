package def;

import java.util.HashSet;
import java.util.Set;

public class ProcessBits {
	private boolean check;
	private String bit;
	private int row;	//まとめ上位何段か
	private Set<String> coverBits = new HashSet<>();	//この文字列が覆ってる文字列


	//だいたい一回目の挿入で使う
	public ProcessBits(String bit) {
		check = false;
		this.bit = bit;
		this.row = 0;
	}

	//処理したやつの再代入で使う
	public ProcessBits(String bit,int row,Set<String> covered) {
		this.check = false;
		this.bit = bit;
		this.row = row;
		this.coverBits.addAll(covered);
	}

	//列を調べる
	int rowCheck() {
		return row;
	}

	//チェックつける
	void checker() {
		check = true;
	}

	//チェックを調べる
	boolean getcheck() {
		return check;
	}

	//bits返す
	String getBits() {
		return this.bit;
	}

	//カバーしてるビットを返す
	Set<String> getCoverBits(){
		return this.coverBits;
	}

	//差が一つか調べる
	boolean DifOne(String target) {
		int count=0;

		for(int i = 0;i < this.bit.length();i++) {

			if(this.bit.charAt(i) != target.charAt(i)) {
				count++;
			}
		}

		if(count == 1) {
			return true;
		}else {
			return false;
		}
	}

	//違いが1であることを知ってから使う
	//違う部分を-"ハイフン"にする。
	//ここでカバーの判定できない？
	ProcessBits replaceDif(ProcessBits target) {
		StringBuilder ret = new StringBuilder();

		for(int i = 0;i < this.bit.length();i++) {
			if(this.bit.charAt(i) == target.bit.charAt(i)) {
				ret.append(this.bit.charAt(i));
			}else {
				ret.append('-');
			}
		}

		Set<String> sumaddcover = new HashSet<>(); //カバーしてるビットたち
		//row=0ではカバーはないので、row=0では自分自身を送る
		if(this.row != 0) {
			//カバーの合成
			sumaddcover.addAll(this.coverBits);
			boolean dob;
			for(String tempta : target.coverBits) {
				dob = false;
				for(String tempth : this.coverBits) {
					if(tempta.equals(tempth)) {
						dob = true;
						break;
					}

				}
				if(!dob) {
					sumaddcover.add(tempta);
				}
			}

		}else {
			sumaddcover.add(this.bit);
			sumaddcover.add(target.bit);

		}
		this.checker();
		target.checker();
		return new ProcessBits(ret.toString(),this.row+1,sumaddcover);
	}

	//純正のequlasが要件を満たさないので新しく作る
	//フィールドが同じなら同じと判定する
	boolean equals(ProcessBits opp) {

		if(this.check == opp.check && this.bit.equals(opp.bit) && this.row == opp.row && this.coverBits.equals(opp.coverBits)) {
			return true;
		}else {
			return false;
		}
	}

	public String toString() {
		StringBuilder ret = new StringBuilder();

		ret.append("bit:"+ this.bit +", row:"+this.row+", check:"+this.check+", covering:"+coverBits);

		return ret.toString();
	}
}
