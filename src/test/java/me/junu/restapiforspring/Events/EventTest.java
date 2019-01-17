package me.junu.restapiforspring.Events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API developement with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        Event event = new Event();
        String name = "Inflearn Spring REST API";
        event.setName(name);
        String spring = "spring";
        event.setDescription(spring);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);
    }

    @Test
    @Parameters(method = "parametersForTestIsFree")
    public void testIsFree(int basePrice, int maxPrice, boolean isFree){
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }
    private Object[] parametersForTestIsFree(){
        return new Object[]{
                new Object[] { 0, 0, true},
                new Object[] { 0, 100, false},
                new Object[] { 100, 0, false},
                new Object[] { 100, 200, false}
        };

    }

    @Test
    public void testIsOffline(){
        //Given
        Event event = Event.builder()
                .location("네이버 D2 팩토리")
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue();

        //Given
        event = Event.builder()
                .build();

        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse();
    }
}