package com.example.yingjianwu.event_recommendation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DatabaseInterface {

    //UserTable
    //check whether an account is already registered
    @Query("SELECT * from UserRecord where userName == :userName")
    public UserRecord canRegister(String userName);

    //check whether successfully log in
    @Query("SELECT * from UserRecord where userName == :userName and passWord == :password")
    public UserRecord canLogin(String userName, String password);

    //once successfully register, push the user's information into the database
    @Insert
    void insertUser(UserRecord... userRecords);

    @Query("DELETE FROM UserRecord")
    public void dropUserTable();


    //Event table
    @Query("SELECT * from Event where eventId == :eventId")
    public Event checkEventExist(String eventId);

    @Insert
    void insertEvent(Event... events);

    @Query("UPDATE Event SET numLikes = numLikes + 1 WHERE eventId == :eventId")
    void incrtLike(String eventId);

    @Query("UPDATE Event SET numLikes = numLikes - 1 WHERE eventId == :eventId")
    void decLike(String eventId);

    @Query("UPDATE Event SET numComments = numComments + 1 WHERE eventId == :eventId")
    void incrComments(String eventId);

    @Query("UPDATE Event SET numComments = numComments - 1 WHERE eventId == :eventId")
    void decComments(String eventId);

    @Query("DELETE FROM Event")
    public void dropEventTable();


    //Like table
        //eventId, userId, genreId
    @Insert
    void insertLike(EventLike event);

    @Query("DELETE from EventLike where userId == :userId and eventId == :eventId")
    void deleteLike(String eventId, String userId);

    @Query("SELECT * from EventLike where userId == :userId and eventId == :eventId")
    EventLike checkLike(String userId, String eventId);

    @Query("DELETE FROM EventLike")
    public void dropEventLikeTable();

    @Query ("SELECT * from EventLike where userId == :userId")
    List<EventLike> getAllLike(String userId);


    //Comments table
        //eventId, userId

    @Query("SELECT * from Comment where eventId == :eventId")
    List<Comment> getAllComments(String eventId);

    @Insert
    void insertComments(Comment... comments);

    @Query("DELETE from Comment where eventId == :eventId and userId == :userId")
    void deleteComments(String userId, String eventId);

    @Query("DELETE FROM Comment")
    public void dropComment();


//    //ToDoItem"
//    @Query("DELETE FROM ToDoItem where userName == :userName and eventId == :eventId")
//    public void deleteToDoList(String userName, String eventId);
//
//
//    @Insert
//    public void insertToDoList(ToDoItem... toDoItems);
//
//
//    @Query("SELECT * from ToDoItem where userName == :userName")
//    public List<ToDoItem> getToDoList(String userName);
//
//    @Query("SELECT * from ToDoItem where userName == :userName and eventId == :eventId")
//    public ToDoItem getToDoItem(String userName, String eventId);
//
//    @Query("UPDATE ToDoItem SET priority = :priority WHERE userName == :userName and eventId == :eventId")
//    public void upDatePriority(int priority, String userName, String eventId);
}
