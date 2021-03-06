package net.thucydides.core.steps;

import net.thucydides.core.annotations.AnnotatedFields;
import net.thucydides.core.annotations.InvalidStepsFieldException;
import net.thucydides.core.annotations.Steps;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to identify ScenarioSteps fields that need to be instantiated.
 * 
 * @author johnsmart
 * 
 */
public class StepsAnnotatedField {

    private Field field;
    
    private static final String NO_ANNOTATED_FIELD_ERROR
        = "No ScenarioSteps field annotated with @Steps was found in the test case.";

    /**
     * Find the first field in the class annotated with the <b>Managed</b> annotation.
     */
    public static List<StepsAnnotatedField> findMandatoryAnnotatedFields(final Class<?> clazz) {

        List<StepsAnnotatedField> annotatedFields = findOptionalAnnotatedFields(clazz);
        if (annotatedFields.isEmpty()) {
            throw new InvalidStepsFieldException(NO_ANNOTATED_FIELD_ERROR);
        }
        return annotatedFields;
    }

    /**
     * Find the fields in the class annotated with the <b>Step</b> annotation.
     */
    public static List<StepsAnnotatedField> findOptionalAnnotatedFields(final Class<?> clazz) {

        List<StepsAnnotatedField> annotatedFields = new ArrayList<StepsAnnotatedField>();
        for (Field field : AnnotatedFields.of(clazz).allFields()) {
            if (fieldIsAnnotated(field)) {
                annotatedFields.add( new StepsAnnotatedField(field));
            }
        }
        return annotatedFields;
    }

    private static boolean fieldIsAnnotated(final Field aField) {
        Steps fieldAnnotation = annotationFrom(aField);
        return (fieldAnnotation != null);
    }

    private static Steps annotationFrom(final Field aField) {
        Steps annotationOnField = null;
        if (isFieldAnnotated(aField)) {
            annotationOnField = aField.getAnnotation(Steps.class);
        }
        return annotationOnField;
    }

    private static boolean isFieldAnnotated(final Field field) {
        return (fieldIsAnnotatedCorrectly(field) && fieldIsRightType(field));
    }

    private static boolean fieldIsRightType(final Field field) {
        return (ScenarioSteps.class.isAssignableFrom(field.getType()));
    }

    private static boolean fieldIsAnnotatedCorrectly(final Field field) {
        return (field.getAnnotation(Steps.class) != null);
    }

    protected StepsAnnotatedField(final Field field) {
        this.field = field;
    }

    public void setValue(final Object testCase, final ScenarioSteps steps) {
        try {
            field.setAccessible(true);
            field.set(testCase, steps);
        } catch (IllegalAccessException e) {
            throw new InvalidStepsFieldException("Could not access or set @Steps field: " + field, e);
        }
    }

    public boolean isInstantiated(final Object testCase) {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(testCase);
            return (fieldValue != null);
        } catch (IllegalAccessException e) {
            throw new InvalidStepsFieldException("Could not access or set @Steps field: " + field, e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ScenarioSteps> getFieldClass() {
        return (Class<? extends ScenarioSteps>) field.getType();
    }
}
