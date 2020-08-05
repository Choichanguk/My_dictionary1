<?php

$dbHost = "localhost:3306";      // 호스트 주소(localhost, 120.0.0.1)
$dbName = "My_Dictionary";      // 데이타 베이스(DataBase) 이름
$dbUser = "root";          // DB 아이디
$dbPass = "937766wns!";        // DB 패스워드

header('Content-Type: text/html; charset=utf-8');
$dbh = new PDO("mysql:host={$dbHost};dbname={$dbName}", $dbUser, $dbPass);
$dbh ->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

$word_index_str = $_POST['word_index'];
$word = $_POST['word'];
$meaning = $_POST['meaning'];
$what = $_POST['what'];
$word_added_index_str = $_POST['word_added_index'];
$edited_meaning = $_POST['edited_meaning'];

if(isset($word_index_str)){
    $word_index = (int) $word_index_str;
}

if(isset($word_added_index_str)){
    $word_added_index = (int) $word_added_index_str;
}

 //추가 단어를 선택된 단어에 저장하는 로직
if(isset($word_index) && isset($word) && isset($meaning)){
    try {
        $sql = "insert into My_Dictionary.user_word_added (word_index, word, meaning) VALUES ('$word_index', '$word', '$meaning');";
        $dbh ->exec($sql);
    }catch (PDOException $exception){
        echo "레코드 추가 실패! : ".$exception->getMessage()."<br>";
    }
}

//단어 목록을 불러오는 로직
elseif(isset($word_index)){
    $stmt = $dbh -> prepare("SELECT * FROM My_Dictionary.user_word_added WHERE word_index= '$word_index'");
    $stmt -> execute();
    $result = $stmt->fetchAll();
    echo json_encode($result, JSON_UNESCAPED_UNICODE);
}

// 단어를 삭제하는 로직
elseif (isset($word_added_index) && $what == 'delete'){
    try {
        $sql = "DELETE FROM My_Dictionary.user_word_added WHERE word_added_index = '$word_added_index'";
        $dbh ->exec($sql);
    }catch (PDOException $exception){
        echo "레코드 추가 실패! : ".$exception->getMessage()."<br>";
    }
}

//직접입력 단어를 update하는 로직
elseif (isset($edited_meaning) && $what == 'update'){
    try {
        $sql = "UPDATE My_Dictionary.user_word_added SET meaning='$edited_meaning' WHERE word_added_index='$word_added_index'";
        $dbh ->exec($sql);
    }catch (PDOException $exception){
        echo "레코드 추가 실패! : ".$exception->getMessage()."<br>";
    }
}

?>