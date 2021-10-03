package com.mood.userservice.service.search;

import com.mood.userservice.dto.UserDto;
import com.mood.userservice.jpa.UserDetailEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class MoodDistance {
    public double search(UserDto userDto1, UserDto userDto2){
        int[] degreeArray = new int[] {18, 90, 162, 234, 306, 18};
        double[] value1 = new double[]{userDto1.getRespect(), userDto1.getContact(),
                userDto1.getDate(), userDto1.getCommunication(), userDto1.getSex()};
        double[] value2 = new double[]{userDto2.getRespect(), userDto2.getContact(),
                userDto2.getDate(), userDto2.getCommunication(), userDto2.getSex()};
        ArrayList<double[]> userData1 = new ArrayList<double[]>();
        ArrayList<double[]> userData2 = new ArrayList<double[]>();
        ArrayList<double[]> intersections = new ArrayList<double[]>();
        ArrayList<double[]> unions = new ArrayList<double[]>();
        for(int i=0; i < degreeArray.length-1; i++){
            userData1.add(new double[]{value1[i]*Math.cos(deg2rad(degreeArray[i])), value1[i]*Math.sin(deg2rad(degreeArray[i]))});
            userData2.add(new double[]{value2[i]*Math.cos(deg2rad(degreeArray[i])), value2[i]*Math.sin(deg2rad(degreeArray[i]))});
        }

        for(int i = 0; i < degreeArray.length-1; i++){
            if(value1[i] > value2[i]){
                intersections.add(userData1.get(i));
                unions.add(userData2.get(i));
            }else if(value1[i] < value2[i]){
                intersections.add(userData2.get(i));
                unions.add(userData1.get(i));
            }else{
                intersections.add(userData2.get(i));
                unions.add(userData1.get(i));
            }
            double[] resultIntersection = intersection(userData1.get(i)[0], userData1.get(i)[1], userData1.get(i)[1], userData1.get(i)[1],
                    userData2.get(i)[0], userData2.get(i)[0], userData2.get(i)[1], userData2.get(i)[1],
                    degreeArray[i], degreeArray[i+1]);
            if(resultIntersection[0]!=0 && resultIntersection[1]!=0){
                intersections.add(resultIntersection);
                unions.add(resultIntersection);
            }
        }

        double [] intersectionsX = new double[intersections.size()];
        double [] intersectionsY = new double[intersections.size()];
        for (int i=0; i < intersections.size(); i++){
            intersectionsX[i] = intersections.get(i)[0];
            intersectionsY[i] = intersections.get(i)[1];
        }
        double intersectionArea = polygonArea(intersectionsX, intersectionsY, intersectionsX.length);
        double [] unionX = new double[unions.size()];
        double [] unionY = new double[unions.size()];
        for (int i=0; i < unions.size(); i++){
            unionX[i] = unions.get(i)[0];
            unionY[i] = unions.get(i)[1];
        }
        double unionArea = polygonArea(unionX,unionY,unionX.length);

        return intersectionArea/unionArea*100;
    }

    public double findUserMoodDistanceOnTotal(UserDto userDto){
        UserDto total = new UserDto();
        total.setCommunication(5);
        total.setContact(5);
        total.setDate(5);
        total.setRespect(5);
        total.setSex(5);
        return search(userDto, total);
    }

    public double polygonArea(double[] X, double[] Y, int numPoints)
    {
        int area = 0;   // Accumulates area
        int j = numPoints-1;

        for (int i=0; i < numPoints; i++)
        { area +=  (X[j]+X[i]) * (Y[j]-Y[i]);
            j = i;  //j is previous vertex to i
        }
        return area/2;
    }

    public double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int degree1, int degree2) {
        int[] degreeArray = new int[] {18, 90, 162, 234, 306, 18};
        double px= (x1*y2 - y1*x2)*(x3-x4) - (x1-x2)*(x3*y4 - y3*x4);
        double py= (x1*y2 - y1*x2)*(y3-y4) - (y1-y2)*(x3*y4 - y3*x4);
        double p = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
        if(p == 0) {
            System.out.println("parallel");
            return new double[]{0 ,0};
        }
        double x = px/p;
        double y = py/p;

        for(int i=0; i < degreeArray.length-1; i++){
            if(-5 <= x && x <=-5 && -5 <= y && y <= 5){
                if((Math.tan(deg2rad(degreeArray[i])*x) < y) && (y < Math.tan(deg2rad(deg2rad(degreeArray[i+1])*x)))){
                    log.info("Intersection : X = "+x+" Y="+y);
                    return new double[]{x, y};
                }
            }
        }
        return new double[]{0 ,0};
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
