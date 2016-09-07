package com.victormunoz.material;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;

import com.victormunoz.material.widget.AnimatorFailure;
import com.victormunoz.material.widget.FloatingProgressActionButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.sql.DriverManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;

//@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(AnimatedVectorDrawableCompat.class)
@RunWith(PowerMockRunner.class)
public class AnimatorFailureTest {

    @Mock
    Context mMockContext;
    @Mock
    Resources mMockResources;
    @Mock
    FloatingProgressActionButton view;
    @Mock
    AnimatedVectorDrawableCompat advToFailure;


    private AnimatorFailure failureAnim;

    @Before
    public void setupAddNotePresenter() throws XmlPullParserException {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
        Mockito.when(mMockContext.getResources()).thenReturn(mMockResources);
        PowerMockito.mockStatic(AnimatedVectorDrawableCompat.class);
        Mockito.when(AnimatedVectorDrawableCompat.create(any(Context.class), any(int.class))).thenReturn(advToFailure);
        failureAnim = new AnimatorFailure(mMockContext, view);


    }


    @Test
    public void readStringFromContext_LocalizedString() {

        // failureAnim.play();
        //verify(failureAnim).resume(0);
       /*
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.hello_word))
                .thenReturn(FAKE_STRING);
        ClassUnderTest myObjectUnderTest = new ClassUnderTest(mMockContext);

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.getHelloWorldString();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
        */
    }
}
