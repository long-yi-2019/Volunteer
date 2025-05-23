package com.example.myapplication;

import androidx.fragment.app.Fragment;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest extends Fragment {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        } finally {

        }

    }
}