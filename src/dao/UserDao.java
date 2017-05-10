package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import domain.FileVo;
import domain.RkVo;
import domain.UserVo;

@Repository
public class UserDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int getMatchCount(String id, String password) {
		String sqlStr = " SELECT count(*) FROM user " + " WHERE id =? and password=? ";
		return jdbcTemplate.queryForInt(sqlStr, new Object[] { id, password });
	}

	public UserVo findUserById(final String id) {
		String sqlStr = " SELECT * " + " FROM user WHERE id =? ";
		final UserVo user = new UserVo();
		jdbcTemplate.query(sqlStr, new Object[] { id }, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				user.setId(rs.getString("id"));
				user.setPassword(rs.getString("password"));
			}
		});
		return user;
	}

	public int registerUser(UserVo userVo) {
		String sqlStr = " insert into user(id,password) values(?,?) ";
		try {
			jdbcTemplate.update(sqlStr, new Object[] { userVo.getId(), userVo.getPassword() });
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

	public List<UserVo> allUsers() {
		String sqlStr = "SELECT * FROM user";
		final List<UserVo> list = new ArrayList<UserVo>();
		jdbcTemplate.query(sqlStr, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				UserVo user = new UserVo();
				user.setId(rs.getString("id"));
				user.setPassword(rs.getString("password"));
				list.add(user);
			}
		});
		return list;
	}

	public void delUser(String id) {
		String sqlStr = "DELETE FROM mb_table WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM mb_cdt WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM mb_show WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM moban WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM user WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
	}

	public void toUser(String id) {
		String sqlStr = "UPDATE user SET role='N' WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
	}

	public void toAdmin(String id) {
		String sqlStr = "UPDATE user SET role='A' WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM mb_table WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM mb_cdt WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM mb_show WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
		sqlStr = "DELETE FROM moban WHERE id = ?";
		jdbcTemplate.update(sqlStr, new Object[] { id });
	}
	
	public boolean insertFile(FileVo fileVo) {
		String sqlStr = " insert into file(id,fileName,date,size,path,desPath,conditionPath) values(?,?,?,?,?,?,?) ";
		String id = fileVo.getId();
		String fileName = fileVo.getFileName();
		String date = fileVo.getDate();
		String size = fileVo.getSize();
		String path = fileVo.getPath();
		String desPath = fileVo.getDesPath();
		String conditionPath = fileVo.getConditionPath();
		
		try {
			jdbcTemplate.update(sqlStr, new Object[] { id, fileName, date, size, path, desPath, conditionPath });
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public List<FileVo> findAllFilesForUser(String id) {
		String sqlStr = "SELECT * FROM file WHERE id =? ";
		final List<FileVo> list = new ArrayList<FileVo>();
		jdbcTemplate.query(sqlStr, new Object[] { id }, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				FileVo file = new FileVo();
				file.setFileName(rs.getString("fileName"));
				file.setSize(rs.getString("size"));
				file.setDate(rs.getString("date"));
				list.add(file);
			}
		});
		return list;
	}
	
	public List<RkVo> findSharedFilesForUser(String id) {
		String sqlStr = "SELECT * FROM rk WHERE author =? ";
		final List<RkVo> list = new ArrayList<RkVo>();
		jdbcTemplate.query(sqlStr, new Object[] { id }, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				RkVo file = new RkVo();
				file.setReceiver(rs.getString("receiver"));
				file.setFileName(rs.getString("fileName"));
				file.setSize(rs.getString("size"));
				file.setDate(rs.getString("date"));
				list.add(file);
			}
		});
		return list;
	}
	
	public List<RkVo> findReceiveFilesForUser(String id) {
		String sqlStr = "SELECT * FROM rk WHERE receiver =? ";
		final List<RkVo> list = new ArrayList<RkVo>();
		jdbcTemplate.query(sqlStr, new Object[] { id }, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				RkVo file = new RkVo();
				file.setAuthor(rs.getString("author"));
				file.setFileName(rs.getString("fileName"));
				file.setSize(rs.getString("size"));
				file.setDate(rs.getString("date"));
				list.add(file);
			}
		});
		return list;
	}
	
	public Map<String, Object> findDESCipherPath(String id, String fileName) {
		String sqlStr = "SELECT desPath FROM file WHERE id =? and fileName=? ";
		Map<String, Object> map = new HashMap<String, Object>();
		map = jdbcTemplate.queryForMap(sqlStr, new Object[] { id, fileName });
		return map;
	}
	
	public Map<String, Object> findCipherPath(String id, String fileName) {
		String sqlStr = "SELECT path FROM file WHERE id =? and fileName=? ";
		Map<String, Object> map = new HashMap<String, Object>();
		map = jdbcTemplate.queryForMap(sqlStr, new Object[] { id, fileName });
		return map;
	}
	
	public Map<String, Object> findConditionPath(String id, String fileName) {
		String sqlStr = "SELECT conditionPath FROM file WHERE id =? and fileName=? ";
		Map<String, Object> map = new HashMap<String, Object>();
		map = jdbcTemplate.queryForMap(sqlStr, new Object[] { id, fileName });
		return map;
	}
	
	public Map<String, Object> findRkPath(String author, String reveiver, String fileName) {
		String sqlStr = "SELECT rkPath FROM rk WHERE author =? and receiver=? and fileName=? ";
		Map<String, Object> map = new HashMap<String, Object>();
		map = jdbcTemplate.queryForMap(sqlStr, new Object[] { author, reveiver, fileName });
		return map;
	}
	
	public RkVo findInfoForRkVo(String id, String fileName) {
		String sqlStr = "SELECT * FROM file WHERE id =? and fileName=? ";
		final RkVo rkVo = new RkVo();
		jdbcTemplate.query(sqlStr, new Object[] { id, fileName }, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				rkVo.setSize(rs.getString("size"));
				rkVo.setPath(rs.getString("path"));
				rkVo.setDesPath(rs.getString("desPath"));
			}
		});
		return rkVo;
	}
	
	public boolean insertRk(RkVo rkVo) {
		String sqlStr = " insert into rk(author,fileName,receiver,date,size,path,desPath,rkPath) values(?,?,?,?,?,?,?,?) ";
		String author = rkVo.getAuthor();
		String receiver = rkVo.getReceiver();
		String fileName = rkVo.getFileName();
		String date = rkVo.getDate();
		String size = rkVo.getSize();
		String path = rkVo.getPath();
		String desPath = rkVo.getDesPath();
		String rkPath = rkVo.getrkPath();
		
		try {
			jdbcTemplate.update(sqlStr, new Object[] { author, fileName, receiver, date, size, path, desPath, rkPath });
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
