package com.kbwrefrigerator.refrigeratorlist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_note.view.*


class NoteFragment : Fragment() {

    // fragment에서 사용할 Context
    private lateinit var noteFragContext: Context

    // fragment에서 사용할 View
    private lateinit var noteFragViewOfLayout: View

    // fragment에서 사용할 context를 얻는 과정(기본적으로 fragment는 Activity가 아니기때문에 context가 없다.)
    // onAttach lifeCycle에서 얻는 것이 getActivity를 이용한 방법보다 안전한 방법이다.(context가 가끔 null 이 되기도 한다.)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        noteFragContext = context    // Root Activity에 접근할 경우 context as Activity 를 이용
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        noteFragViewOfLayout = inflater.inflate(R.layout.fragment_note, container, false)

        // toolbar
        var toolbar = noteFragViewOfLayout.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (noteFragContext as MainActivity).setSupportActionBar(toolbar)  // mainFragContext context로 MainActivity를 불러와 사용
        var actionBar = (noteFragContext as MainActivity).supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌

        val notePref = (noteFragContext as MainActivity).getSharedPreferences("note", 0)

        noteFragViewOfLayout.noteView.setText(notePref.getString("key", ""))

        return noteFragViewOfLayout
    }

    override fun onPause() {
        super.onPause()
        val notePref = (noteFragContext as MainActivity).getSharedPreferences("note", 0)
        val noteEditor = notePref.edit()

        noteEditor.putString("key", noteFragViewOfLayout.noteView.text.toString())
        noteEditor.apply()
    }
}