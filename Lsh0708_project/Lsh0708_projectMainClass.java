package Lsh0708_project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class Lsh0708_projectMainClass extends JFrame {

	Lsh0708_Project_DAO dao = new Lsh0708_Project_DAO();

	private JComboBox<String> deptCombo;
	private JComboBox<String> departmentField;
	private JPanel SearchPanel;
	private JPanel SearchDetailPanel;
	private JPanel ViewPanel;
	private JPanel ResultPanel;
	private JPanel UpdatePanel;
	private JPanel MainPanel;
	private JTable ViewTable;
	private JLabel searchResult;
	private JTextField nameField;
	private JTextField birthdateField;
	private JTextField addressField;
	private JTextField phoneField;
	private ButtonGroup genderGroup;
	private JRadioButton maleButton;
	private JRadioButton femaleButton;
	private Object[][] resultData;
	private ArrayList<JCheckBox> checkBoxList;
	private ArrayList<String> selectedOptions;
	private ArrayList<String> nullRemoveList;
	private ArrayList<String> resultList;

	private String[] columnNames_KOR = { "사번", "이름", "부서", "생년월일", "주소", "전화번호", "성별" };
	private String[] columnNames_ENG = { "id", "name", "department", "birthdate", "address", "telNum", "sex" };
	private String[] deptColumn = { "전체", "연구소", "생산부", "영업부", "관리부" };

	public Lsh0708_projectMainClass() {

		setTitle("사내 직원 검색 프로그램 V 1.0.0");
		// 창 사이즈. 기본 크기.
		setSize(1000, 600);
		setLocationRelativeTo(null); // 화면 가운데 정렬
		// 창의 닫기를 클릭시, 정상 종료.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		MainPanel = new JPanel();
		MainPanel.setLayout(new BoxLayout(MainPanel, BoxLayout.Y_AXIS));

		SearchPanel = new JPanel();
		// SearchPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		SearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		SearchPanel.add(new JLabel("검색 범위"));
		SearchPanel.setPreferredSize(new Dimension(1000, 50));
		deptCombo = new JComboBox<String>();
		SearchPanel.add(deptCombo);
		for (int i = 0; i < deptColumn.length; i++) {
			deptCombo.addItem(deptColumn[i]);
		}

		SearchDetailPanel = new JPanel();
		// SearchDetailPanel.setBorder(BorderFactory.createLineBorder(Color.PINK));
		SearchDetailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		SearchDetailPanel.setPreferredSize(new Dimension(1000, 50));
		SearchDetailPanel.add(new JLabel("검색 항목"));
		checkBoxList = new ArrayList<>();
		selectedOptions = new ArrayList<>();

		for (int i = 0; i < columnNames_ENG.length; i++) {
			selectedOptions.add(columnNames_ENG[i]);
		}

		for (int i = 0; i < columnNames_KOR.length; i++) {
			JCheckBox checkBox = new JCheckBox(columnNames_KOR[i], true);
			// 사번은 무조건 검색되도록 체크박스 체크상태 변경 불가
			if (i == 0) {
				checkBox.setEnabled(false);
			}
			checkBoxList.add(checkBox);
			checkBox.addItemListener(createItemListener(i));
			checkBoxList.add(checkBox);
			SearchDetailPanel.add(checkBox);

		}

		JButton SerchButton = new JButton("검색");
		SearchDetailPanel.add(SerchButton);
		SearchDetailPanel.setPreferredSize(new Dimension(1000, 50));

		ViewPanel = new JPanel();
		// ViewPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		ViewPanel.setLayout(new FlowLayout());
		ViewPanel.setPreferredSize(new Dimension(1000, 400));

		ResultPanel = new JPanel();
		// ResultPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		ResultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchResult = new JLabel("검색 결과 수: ");
		Font font = new Font("돋움", Font.BOLD, 20);
		searchResult.setFont(font);
		ResultPanel.add(searchResult);
		ResultPanel.setPreferredSize(new Dimension(1000, 50));

		UpdatePanel = new JPanel();
		// UpdatePanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		UpdatePanel.setLayout(new FlowLayout());
		UpdatePanel.add(new JLabel("새로운 직원 추가"));
		JButton addButton = new JButton("추가");
		UpdatePanel.add(addButton);
		UpdatePanel.add(new JLabel("선택한 데이터 삭제"));
		JButton deleteButton = new JButton("삭제");
		UpdatePanel.add(deleteButton);
		UpdatePanel.setSize(1000, 200);

		UpdatePanel.add(new JLabel("검색 결과 파일로 저장"));
		JButton fileSaveButton = new JButton("저장");
		UpdatePanel.add(fileSaveButton);

		add(MainPanel);
		MainPanel.add(SearchPanel);
		MainPanel.add(SearchDetailPanel);
		MainPanel.add(ViewPanel);
		MainPanel.add(ResultPanel);
		MainPanel.add(UpdatePanel);

		addButton.addActionListener(e -> addUserWindow());
		SerchButton.addActionListener(e -> selectedDept((String) deptCombo.getSelectedItem(), selectedOptions));
		deleteButton.addActionListener(e -> deleteSelectedRows());
		fileSaveButton.addActionListener(e -> saveAsFile(resultData));
		setVisible(true);
	}

	// 직원 추가 화면
	private void addUserWindow() {
		JFrame newFrame = new JFrame("직원 추가");
		newFrame.setSize(300, 300);
		newFrame.setLocationRelativeTo(null);
		newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 새 창만 닫기
		newFrame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5); // 패딩 설정

		// 입력 필드 및 레이블 생성
		gbc.gridx = 0;
		gbc.gridy = 0;
		newFrame.add(new JLabel("이름:"), gbc);

		gbc.gridx = 1;
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(200, 25));
		newFrame.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		newFrame.add(new JLabel("부서:"), gbc);
		gbc.gridx = 1;
		departmentField = new JComboBox<String>();
		for (int i = 1; i < deptColumn.length; i++) {
			departmentField.addItem(deptColumn[i]);
		}
		departmentField.setPreferredSize(new Dimension(200, 25));
		newFrame.add(departmentField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		newFrame.add(new JLabel("생년월일 (YYYY-MM-DD):"), gbc);
		gbc.gridx = 1;
		birthdateField = new JTextField();
		birthdateField.setEditable(false);
		birthdateField.setPreferredSize(new Dimension(200, 25));
		newFrame.add(birthdateField, gbc);

		// 생년월일 필드 선택시 날짜 선택 툴 실행
		birthdateField.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showDatePicker();
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 3;
		newFrame.add(new JLabel("주소:"), gbc);
		gbc.gridx = 1;
		addressField = new JTextField();
		addressField.setPreferredSize(new Dimension(200, 25));
		newFrame.add(addressField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		newFrame.add(new JLabel("전화 번호:"), gbc);
		gbc.gridx = 1;
		phoneField = new JTextField();
		phoneField.setPreferredSize(new Dimension(200, 25));
		newFrame.add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		newFrame.add(new JLabel("성별:"), gbc);

		// 성별을 위한 라디오 버튼 생성
		maleButton = new JRadioButton("남");
		maleButton.setSelected(true);
		femaleButton = new JRadioButton("여");
		genderGroup = new ButtonGroup(); // 라디오 버튼 그룹 생성
		genderGroup.add(maleButton);
		genderGroup.add(femaleButton);

		// 라디오 버튼을 패널에 추가
		JPanel genderPanel = new JPanel();
		genderPanel.add(maleButton);
		genderPanel.add(femaleButton);
		gbc.gridx = 1;
		gbc.gridy = 5;
		newFrame.add(genderPanel, gbc);

		// 사용자 추가 버튼 생성
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2; // 버튼이 두 열을 차지하도록 설정
		gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬
		JButton addUserButton = new JButton("신규 직원 추가");
		addUserButton.setPreferredSize(new Dimension(120, 40));
		newFrame.add(addUserButton, gbc);

		addUserButton.addActionListener(e -> addUser());

		newFrame.setVisible(true);
	}

	// 직원 정보 수정 화면
	private void modifyUserWindow(int userID) {
		Lsh0708_project_DTO list = dao.selectOne(userID);
		JFrame newFrame = new JFrame("직원 정보 수정");
		newFrame.setSize(300, 300);
		newFrame.setLocationRelativeTo(null);
		newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 새 창만 닫기
		newFrame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5); // 패딩 설정

		// 입력 필드 및 레이블 생성
		gbc.gridx = 0;
		gbc.gridy = 0;
		newFrame.add(new JLabel("이름:"), gbc);

		gbc.gridx = 1;
		nameField = new JTextField();
		nameField.setText(list.getName());
		nameField.setPreferredSize(new Dimension(250, 25));
		newFrame.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		newFrame.add(new JLabel("부서:"), gbc);
		gbc.gridx = 1;
		departmentField = new JComboBox<String>();
		for (int i = 1; i < deptColumn.length; i++) {
			departmentField.addItem(deptColumn[i]);
		}
		departmentField.setPreferredSize(new Dimension(250, 25));
		newFrame.add(departmentField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		newFrame.add(new JLabel("생년월일 (YYYY-MM-DD):"), gbc);
		gbc.gridx = 1;
		birthdateField = new JTextField();
		birthdateField.setText(list.getBirthdate());
		birthdateField.setEditable(false);
		birthdateField.setPreferredSize(new Dimension(250, 25));
		newFrame.add(birthdateField, gbc);

		birthdateField.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showDatePicker();
			}
		});

		gbc.gridx = 0;
		gbc.gridy = 3;
		newFrame.add(new JLabel("주소:"), gbc);
		gbc.gridx = 1;
		addressField = new JTextField();
		addressField.setText(list.getAddress());
		addressField.setPreferredSize(new Dimension(250, 25));
		newFrame.add(addressField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		newFrame.add(new JLabel("전화 번호:"), gbc);
		gbc.gridx = 1;
		phoneField = new JTextField();
		phoneField.setText(list.getTelNum());
		phoneField.setPreferredSize(new Dimension(250, 25));
		newFrame.add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		newFrame.add(new JLabel("성별:"), gbc);
		// 성별을 위한 라디오 버튼 생성
		maleButton = new JRadioButton("남");
		femaleButton = new JRadioButton("여");
		System.out.println(list.getSex());
		if (list.getSex().equals("Male")) {
			maleButton.setSelected(true);
		} else {
			femaleButton.setSelected(true);
		}
		genderGroup = new ButtonGroup(); // 라디오 버튼 그룹 생성
		genderGroup.add(maleButton);
		genderGroup.add(femaleButton);

		// 라디오 버튼을 패널에 추가
		JPanel genderPanel = new JPanel();
		genderPanel.add(maleButton);
		genderPanel.add(femaleButton);
		gbc.gridx = 1;
		gbc.gridy = 5;
		newFrame.add(genderPanel, gbc);

		// 사용자 수정 버튼 생성
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2; // 버튼이 두 열을 차지하도록 설정
		gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬
		JButton modifyUserButton = new JButton("수정");
		modifyUserButton.setPreferredSize(new Dimension(120, 40));
		newFrame.add(modifyUserButton, gbc);

		modifyUserButton.addActionListener(e -> modifyUser(userID));

		newFrame.setVisible(true); // 새 창 표시
	}

	// 검색항목 체크박스 체크 여부 확인
	private ItemListener createItemListener(int index) {
		return e -> {
			String checkItemName = "";
			JCheckBox checkBox = (JCheckBox) e.getItem();

			if (e.getStateChange() == ItemEvent.SELECTED) {
				switch (checkBox.getText()) {
				case "사번":
					checkItemName = "id";
					break;
				case "이름":
					checkItemName = "name";
					break;
				case "부서":
					checkItemName = "department";
					break;
				case "생년월일":
					checkItemName = "birthdate";
					break;
				case "주소":
					checkItemName = "address";
					break;
				case "전화번호":
					checkItemName = "telNum";
					break;
				case "성별":
					checkItemName = "sex";
					break;
				}
				selectedOptions.set(index, checkItemName); // 체크된 항목 추가
			} else {
				switch (checkBox.getText()) {
				case "사번":
					checkItemName = "id";
					break;
				case "이름":
					checkItemName = "name";
					break;
				case "부서":
					checkItemName = "department";
					break;
				case "생년월일":
					checkItemName = "birthdate";
					break;
				case "주소":
					checkItemName = "address";
					break;
				case "전화번호":
					checkItemName = "telNum";
					break;
				case "성별":
					checkItemName = "sex";
					break;
				}
				selectedOptions.set(index, null); // 해제된 항목 제거
			}
			// 선택된 항목 리스트 출력
			nullRemoveList = new ArrayList<String>();
			for (String item : selectedOptions) {
				if (item != null) {
					nullRemoveList.add(item);
				}
			}
		};
	}

	// 날짜 선택 툴
	private void showDatePicker() {
		UtilDateModel model = new UtilDateModel();
		Properties properties = new Properties();
		properties.put("text.today", "오늘");
		properties.put("text.month", "월");
		properties.put("text.year", "년");

		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);

		int result = JOptionPane.showConfirmDialog(this, datePicker, "날짜 선택", JOptionPane.OK_CANCEL_OPTION);

		// 날짜 선택 시 텍스트 필드에 날짜를 설정
		if (result == JOptionPane.OK_OPTION) {
			java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
			if (selectedDate != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				birthdateField.setText(dateFormat.format(selectedDate));
			}
		}

	}

	private void selectedDept(String dept, ArrayList<String> nullRemoveList) {

		if (nullRemoveList == null) {
			nullRemoveList = new ArrayList<>(); // 빈 리스트로 초기화
		}
		// 사번 이외에 하나 이상의 검색 항목 선택하도록 하는 기능
		int checkedCount = (int) nullRemoveList.stream().filter(Objects::nonNull).count();
		if (checkedCount < 2) {
			JOptionPane.showMessageDialog(this, "사번 이외 하나 이상의 항목을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		ArrayList<Lsh0708_project_DTO> list = dao.select(dept, nullRemoveList);
		if (list == null || list.isEmpty()) {
			JOptionPane.showMessageDialog(this, "데이터가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		searchResult.setText("검색 결과 수: " + list.size());
		System.out.println("검색 결과 수 :" + list.size());

		// 체크된 항목 수에 맞는 배열 생성
		resultData = new Object[list.size()][checkedCount];

		// 체크된 항목의 인덱스를 관리하기 위한 리스트
		ArrayList<Integer> checkedIndices = new ArrayList<>();
		resultList = new ArrayList<String>();
		for (int j = 0; j < nullRemoveList.size(); j++) {
			if (nullRemoveList.get(j) != null) {
				checkedIndices.add(j);
				switch (nullRemoveList.get(j)) {
				case "id":
					resultList.add("사번");
					break;
				case "name":
					resultList.add("이름");
					break;
				case "department":
					resultList.add("부서");
					break;
				case "birthdate":
					resultList.add("생년월일");
					break;
				case "address":
					resultList.add("주소");
					break;
				case "telNum":
					resultList.add("전화번호");
					break;
				case "sex":
					resultList.add("성별");
					break;
				default:
					break;
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			Lsh0708_project_DTO dto = list.get(i);

			for (int j = 0; j < checkedIndices.size(); j++) {
				int index = checkedIndices.get(j);
				String column = nullRemoveList.get(index);

				switch (column) {
				case "id":
					resultData[i][j] = dto.getId();
					break;
				case "name":
					resultData[i][j] = dto.getName();
					break;
				case "department":
					resultData[i][j] = dto.getDepartment();
					break;
				case "birthdate":
					resultData[i][j] = dto.getBirthdate();
					break;
				case "address":
					resultData[i][j] = dto.getAddress();
					break;
				case "telNum":
					resultData[i][j] = dto.getTelNum();
					break;
				case "sex":
					resultData[i][j] = dto.getSex();
					break;
				default:
					break;
				}
			}

		}

		updateTable(resultData, resultList);
	}

	// 직원 추가
	private void addUser() {
		if (nameField.getText().isEmpty() || birthdateField.getText().isEmpty() || addressField.getText().isEmpty()
				|| phoneField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "모든 필드를 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
			return; // 메서드 종료
		}
		String userName = nameField.getText();
		String userDept = (String) departmentField.getSelectedItem();
		String userBirth = birthdateField.getText();
		String userAdd = addressField.getText();
		String userTelNum = phoneField.getText();
		String userSex;
		if (maleButton.isSelected()) {
			userSex = "Male";
		} else {
			userSex = "Female";
		}

		int result = dao.insertDB(userName, userDept, userBirth, userAdd, userTelNum, userSex);
		JOptionPane.showMessageDialog(null, userName + "님을 추가했습니다.", "추가 완료", JOptionPane.INFORMATION_MESSAGE);

		System.out.println("사용자 " + userName + " 추가됨.");
		nameField.setText("");
		birthdateField.setText("");
		addressField.setText("");
		phoneField.setText("");
		maleButton.setSelected(true);

	}

	// 테이블 맨 앞 열에 체크박스 추가 기능
	public class CheckBoxTableModel extends DefaultTableModel {
		public CheckBoxTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) { // 첫 번째 열에 체크박스를 추가
				return Boolean.class; // Boolean 타입으로 설정
			}
			return super.getColumnClass(columnIndex);
		}
	}

	// 테이블 생성 후 새로 그리기
	private void updateTable(Object[][] data, ArrayList<String> nullRemoveList) {
		String[] columnNames = new String[nullRemoveList.size() + 1];
		columnNames = nullRemoveList.toArray(columnNames);
		columnNames[0] = "선택";
		for (int i = 0; i < nullRemoveList.size(); i++) {
			columnNames[i + 1] = nullRemoveList.get(i);
		}

		// 체크박스 컬럼 추가를 위한 데이터 배열 생성 (체크된 검색항목 + 1)
		Object[][] newData = new Object[data.length][nullRemoveList.size() + 1];
		for (int i = 0; i < data.length; i++) {
			newData[i][0] = false; // 체크박스 초기값을 false로 설정
			System.arraycopy(data[i], 0, newData[i], 1, data[i].length);
		}

		// 체크박스 테이블 모델 생성
		CheckBoxTableModel model = new CheckBoxTableModel(newData, columnNames);
		ViewTable = new JTable(model) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0; // 체크박스 컬럼만 편집 가능
			}
		};

		// 테이블 더블 클릭시 이벤트 발생
		ViewTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// 더블 클릭 여부 확인 - 클릭횟수 2회
				if (e.getClickCount() == 2) {
					int row = ViewTable.rowAtPoint(e.getPoint());

					// 특정 셀을 더블 클릭했을 때 이벤트 실행
					// 예시: 더블 클릭한 직원의 ID를 가져와서 수정 작업
					Object employeeId = ViewTable.getValueAt(row, 1); // ID가 두 번째 열에 있다고 가정
					modifyUserWindow((int) employeeId);
					System.out.println("선택된 직원 ID: " + employeeId);
				}
			}
		});

		// 테이블 항목들 가운데 정렬
		CenteredTableCellRenderer centeredRenderer = new CenteredTableCellRenderer();
		for (int i = 1; i < model.getColumnCount(); i++) {
			ViewTable.getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
		}

		ViewTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 모든 열 크기를 화면크기에 맞춤
		JScrollPane scrollPane = new JScrollPane(ViewTable);
		ViewPanel.setLayout(new BorderLayout());
		ViewPanel.removeAll(); // 기존 컴포넌트를 제거
		ViewPanel.add(scrollPane, BorderLayout.CENTER);
		ViewPanel.revalidate();
		ViewPanel.repaint();
	}

	public class CenteredTableCellRenderer extends DefaultTableCellRenderer {
		public CenteredTableCellRenderer() {
			setHorizontalAlignment(JLabel.CENTER); // 텍스트를 가운데 정렬
		}
	}

	// 사용자 정보 수정
	private void modifyUser(int userID) {
		if (nameField.getText().isEmpty() || birthdateField.getText().isEmpty() || addressField.getText().isEmpty()
				|| phoneField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "모든 필드를 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
			return; // 메서드 종료
		}
		String userName = nameField.getText();
		String userDept = (String) departmentField.getSelectedItem();
		String userAdd = addressField.getText();
		String userTelNum = phoneField.getText();
		String userSex;
		if (maleButton.isSelected()) {
			userSex = "Male";
		} else {
			userSex = "Female";
		}

		int confirm = JOptionPane.showConfirmDialog(this, "사용자 정보를 수정하시겠습니까?", "사용자 정보 수정", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		} else {
			// DB에서 선택된 ID에 해당하는 데이터를 수정
			int modCount = dao.modifyDB(userID, userName, userDept, userAdd, userTelNum, userSex);
			JOptionPane.showMessageDialog(this, modCount + "개의 데이터 수정되었습니다.", "수정 완료", JOptionPane.INFORMATION_MESSAGE);

		}
	}

	private void deleteSelectedRows() {
		// 선택된 데이터가 없을 경우 알림 메시지
		if (ViewTable == null || ViewTable.getModel() == null ) {
	        JOptionPane.showMessageDialog(this, "검색을 먼저 진행하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }
		ArrayList<String> idsToDelete = new ArrayList<>();
		CheckBoxTableModel model = (CheckBoxTableModel) ViewTable.getModel();	

		// 선택된 행의 id 값을 추출
		for (int i = 0; i < model.getRowCount(); i++) {
			Boolean isChecked = (Boolean) model.getValueAt(i, 0);
			if (isChecked != null && isChecked) {
				String id = model.getValueAt(i, 1).toString();
				idsToDelete.add(id);
			}
		}
		if (idsToDelete.isEmpty()) {
			JOptionPane.showMessageDialog(this, "삭제할 데이터를 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 삭제 확인 다이얼로그
		int confirm = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		} else {
			// DB에서 선택된 ID에 해당하는 데이터를 삭제
			int deleteCount = dao.deleteUser(idsToDelete);
			JOptionPane.showMessageDialog(this, deleteCount + "개의 데이터가 삭제되었습니다.", "삭제 완료",
					JOptionPane.INFORMATION_MESSAGE);

		}

	}

	// JFileChooser를 사용하여 파일을 다른 이름으로 저장하는 메서드
	public void saveAsFile(Object[][] data) {
		if (data == null) {
			JOptionPane.showMessageDialog(null, "검색 진행 후 저장 가능합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("다른 이름으로 저장");

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("저장할 파일: " + fileToSave.getAbsolutePath());
			saveToFile(fileToSave, data); // 파일에 데이터 저장
		} else {
			System.out.println("파일 저장이 취소되었습니다.");
		}
	}

	private void saveToFile(File file, Object[][] data) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			String[] header = { "ID", "Name", "Department", "Birthdate", "Address", "Telephone", "Sex" };
			writer.write(String.join(",", header));
			writer.newLine();
			// CSV 데이터 작성
			for (Object[] row : data) {
				StringBuilder line = new StringBuilder();
				for (Object cell : row) {
					line.append(cell != null ? cell.toString() : "").append(",");
				}
				// 마지막에 추가된 콤마 제거
				writer.write(line.substring(0, line.length() - 1));
				writer.newLine();
			}
			JOptionPane.showMessageDialog(null, "파일을 저장했습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("데이터 파일 저장 성공: " + file.getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "파일 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new Lsh0708_projectMainClass();
	}

}
