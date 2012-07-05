package com.miquniqu.android.twiccaplugins.zusaar;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {
	private Context context;
	private LayoutInflater inflater;
	private int layoutId;
	private List<User> items;

	public UserAdapter(Context _context, int _layoutId, List<User> _items) {
		super(_context, _layoutId, _items);

		this.context = _context;
		this.layoutId = _layoutId;
		this.items = _items;
		inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		UserHolder holder;
		// 対象のアイテムを取得
		User data = items.get(_position);

		// convertViewがnullなら新規作成
		if (_convertView == null) {
			_convertView = inflater.inflate(layoutId, _parent, false);

			holder = new UserHolder();
			holder.textNo = (TextView) _convertView.findViewById(R.id.text_no);
			holder.textNickname = (TextView) _convertView.findViewById(R.id.text_nickname);
			holder.textStatus = (TextView) _convertView.findViewById(R.id.text_status);

			_convertView.setTag(holder);
		}
		// convertViewが渡されたらそれを利用、なかったら新規作成
		else {
			// view = convertView;
			holder = (UserHolder) _convertView.getTag();
		}

		// アイテムを保存する
		holder.userItem = data;

		//番号
		int no = _position + 1;
		holder.textNo.setText(String.valueOf(no));
		//ニックネーム
		holder.textNickname.setText("@" + data.nickname);
		// 出席状態
		int status = Integer.parseInt(data.status);
		if (status == User.STATUS_ACCEPTED) {
			holder.textStatus.setText(context.getResources().getString(R.string.itemname_user_status_accepted));
		} else if (status == User.STATUS_WAITING) {
			holder.textStatus.setText(context.getResources().getString(R.string.itemname_user_status_waiting));
		}

		return _convertView;
	}

	// イベント情報のホルダー
	static class UserHolder {
		public TextView textNo;
		public TextView textNickname;
		public TextView textStatus;
		public User userItem;
	}

}