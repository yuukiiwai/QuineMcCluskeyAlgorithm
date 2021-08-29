package def;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainClause {
	/*
	 * 一候補を保存するためのクラスなので、メインではリスト化して使用する。
	 */
	//private int addNum;	//上位段階のまとめてるビットからいくつ増やせているか
	private Set<ProcessBits> candiSet = new HashSet<>(); //まとめビットを束にするためのセット

	public MainClause() {

	}

	/*
	 * 一候補の要素を挿入していく
	 * 要素が2つで全ビットをカバーできるか試しているなら
	 * 2回呼び出して使用する。
	 */
	public void addBit(ProcessBits aCandi) {
		candiSet.add(aCandi);
	}

	//このインスタンスで全部のデータを表現できているか
	//引数は入力されたデータ
	//動作している
	public boolean correctAllCoverBit(List<String> originList) {
		boolean result = true;

		Set<String> coverd = new HashSet<>(); //今まとめているやつの文字列情報

		for(ProcessBits me : this.candiSet) {
			coverd.addAll(me.getCoverBits());
		}

		//一つでも同じものがなければfalseにする。
		for(String base : originList) {
			if(!coverd.contains(base)) {
				result = false;
			}
		}

		return result;
	}

	//デバッグ用
	//情報を全て表示
	public String toString() {
		StringBuilder ret = new StringBuilder();

		ret.append("{candiSet:\n");
		for(ProcessBits temp:this.candiSet) {
			ret.append(temp.toString()+",\n");
		}
		ret.append("}");

		return ret.toString();
	}

	public String getBits() {
		StringBuilder ret = new StringBuilder();

		ret.append("!----------\n");

		for(ProcessBits temp:this.candiSet) {
			ret.append(temp.getBits()+",\n");
		}

		ret.append("----------!\n");

		return ret.toString();
	}

	public boolean equals(MainClause target) {
		boolean equal = true;

		if(this.candiSet.size() != target.candiSet.size()) {
			return false;
		}
		//上を通過できなければreturn が行くのでelseは要らない

		boolean[] BT = new boolean[this.candiSet.size()];
		int i;

		for(i = 0;i<BT.length;i++) {
			BT[i] = false;
		}

		i = 0;
		for(ProcessBits base : this.candiSet) {
			if(target.candiSet.contains(base)) {
				BT[i] = true;
			}
			i++;
		}

		for(i = 0;i < BT.length;i++) {
			equal = equal && BT[i];
		}
		return equal;
	}
}
