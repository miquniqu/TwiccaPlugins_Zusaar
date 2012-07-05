package com.miquniqu.android.twiccaplugins.zusaar;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<Event> {
	private Context context;
	private LayoutInflater inflater;
	private int layoutId;
	private List<Event> items;

	public EventAdapter(Context _context, int _layoutId, List<Event> _items) {
		super(_context, _layoutId, _items);

		this.context = _context;
		this.layoutId = _layoutId;
		this.items = _items;
		inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		EventHolder holder;
		// 対象のアイテムを取得
		Event data = items.get(_position);

		// convertViewがnullなら新規作成
		if (_convertView == null) {
			_convertView = inflater.inflate(layoutId, _parent, false);

			holder = new EventHolder();
			holder.textTitle = (TextView) _convertView.findViewById(R.id.text_title);
			holder.textCopy = (TextView) _convertView.findViewById(R.id.text_copy);
			holder.textAddress = (TextView) _convertView.findViewById(R.id.text_address);
			holder.textDate = (TextView) _convertView.findViewById(R.id.text_date);
			holder.textPayType = (TextView) _convertView.findViewById(R.id.text_pay_type);
			holder.textOwnerNickname = (TextView) _convertView.findViewById(R.id.text_owner_nickname);
			holder.textCount = (TextView) _convertView.findViewById(R.id.text_count);

			_convertView.setTag(holder);
		}
		// convertViewが渡されたらそれを利用、なかったら新規作成
		else {
			// view = convertView;
			holder = (EventHolder) _convertView.getTag();
		}

		// アイテムを保存する
		holder.eventItem = data;

		holder.textTitle.setText(data.title);

		if (data.catchcopy != null && data.catchcopy.length() != 0) {
			holder.textCopy.setText(data.catchcopy);
		} else {
			holder.textCopy.setText(context.getResources().getString(R.string.itemname_none));
		}

		// 住所
		String address = context.getResources().getString(R.string.itemname_address) + data.address;
		holder.textAddress.setText(address);

		// 開催日時
		if (data.started_at != null && data.ended_at != null) {
			StringBuffer sbDate = new StringBuffer();
			sbDate.append(context.getResources().getString(R.string.itemname_date));
			sbDate.append(LocaleUtil.convertYYYYMMDDHHMM(data.started_at));
			sbDate.append(context.getResources().getString(R.string.itemname_datesep));
			sbDate.append(LocaleUtil.convertHHMM(data.ended_at));
			holder.textDate.setText(sbDate.toString());
		} else {
			holder.textDate.setText(context.getResources().getString(R.string.itemname_datenone));
		}

		// 主催者
		String owner = "@" + data.owner_nickname;
		holder.textOwnerNickname.setText(owner);

		// 支払い種別
		int paytype = Integer.parseInt(data.pay_type);
		if (paytype == Event.PAY_TYPE_MURYOU) {
			holder.textPayType.setText(context.getResources().getString(R.string.itemname_pay_type_0));
		} else if (paytype == Event.PAY_TYPE_YURYOU_KAI) {
			holder.textPayType.setText(context.getResources().getString(R.string.itemname_pay_type_1));
		} else if (paytype == Event.PAY_TYPE_YURYOU_MAE) {
			holder.textPayType.setText(context.getResources().getString(R.string.itemname_pay_type_2));
		}

		// 人数
		StringBuffer sbCount = new StringBuffer();
		sbCount.append("[");
		sbCount.append(context.getResources().getString(R.string.itemname_limit));
		sbCount.append(data.limit);
		sbCount.append("/");
		sbCount.append(context.getResources().getString(R.string.itemname_accepted));
		sbCount.append(data.accepted);
		sbCount.append("/");
		sbCount.append(context.getResources().getString(R.string.itemname_waiting));
		sbCount.append(data.waiting);
		sbCount.append("]");
		holder.textCount.setText(sbCount.toString());

		return _convertView;
	}

	// イベント情報のホルダー
	static class EventHolder {
		public TextView textTitle;
		public TextView textCopy;
		public TextView textAddress;
		public TextView textDate;
		public TextView textPayType;
		public TextView textOwnerNickname;
		public TextView textCount;
		public Event eventItem;
	}

}