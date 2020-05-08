package cn.net.comsys.weixin;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

public class JieMi {
	public static void main(String[] args) throws SQLException {
		Entity entity = Db.use().queryOne("select data from tb_infrared where key_id in (select id from tb_key where  remote_id = '00002000d2ba0019' )");
		Blob blob = entity.getBlob("data");
		try {
			System.out.println(new String(blob.getBytes(1, (int) blob.length()),"GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
