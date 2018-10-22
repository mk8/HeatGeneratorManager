package com.torosoft.heater.heatermanager

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_daily_config.view.*
//import kotlinx.android.synthetic.main.fragment_test.view.*
import android.widget.Button

//import android.support.test.espresso.web.model.Atoms.getTitle
//import android.widget.RelativeLayout
//import android.widget.TextView



class SettingsActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    2 -> {
                        var moon = super@SettingsActivity.findViewById<Button>(R.id.moonTemperature)
                        moon?.setOnClickListener {
                            var dialogFragment = SetTemperatureDialogFragment.newInstance(Integer.valueOf(moon.text.toString())) {
                                moon.text = it.toString()
                            }
                            dialogFragment.show(fragmentManager, "Set default temperature for MOON")
                        }

                        var sun = super@SettingsActivity.findViewById<Button>(R.id.sunTemperature)
                        sun?.setOnClickListener {
                            var dialogFragment = SetTemperatureDialogFragment.newInstance(Integer.valueOf(sun.text.toString())) {
                                sun.text = it.toString()
                            }
                            dialogFragment.show(fragmentManager, "Set default temperature for SUN")
                        }
                    }
                    else -> {
                    }

                }

                TabLayout.TabLayoutOnPageChangeListener(tabs)
            }
        })
        //container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            var rootView: View
            when (arguments?.getInt(ARG_SECTION_NUMBER)) {
                1 -> {
                    rootView = inflater.inflate(R.layout.fragment_daily_config, container, false)

                    val sampleStringList = arrayOf("Busy day", "Busy day with morning", "Weekend", "Vacation")
                    val spinnerArrayAdapter = ArrayAdapter<String>(
                            activity, R.layout.spinner_item, sampleStringList
                    )
                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item)
                    rootView.sunday_spinner.adapter = spinnerArrayAdapter
                    rootView.monday_spinner.adapter = spinnerArrayAdapter
                    rootView.tuesday_spinner.adapter = spinnerArrayAdapter
                    rootView.wednesday_spinner.adapter = spinnerArrayAdapter
                    rootView.thursday_spinner.adapter= spinnerArrayAdapter
                    rootView.friday_spinner.adapter = spinnerArrayAdapter
                    rootView.saturday_spinner.adapter = spinnerArrayAdapter
                }
                2 -> {
                    rootView = inflater.inflate(R.layout.fragment_profiles_config, container, false)
                }
                else -> {
                    rootView = inflater.inflate(R.layout.fragment_general_config, container, false)
                }
            }
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
