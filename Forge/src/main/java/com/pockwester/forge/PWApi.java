package com.pockwester.forge;

/**
 * Created by AW on 10/2/13.
 *
 * Interface that will respond with the PWApi task information
 */
public interface PWApi{

    // Enumeration of the PWApi tasks. This allows consolidation of the api tasks rather than
    // having to manually type in each task.
    public enum TASKS {
        LOGIN("login"),
        CREATE_STUDENT("create_student"),
        COURSE_SEARCH("grab_courses"),
        INSTANCE_SEARCH("grab_instances"),
        UPDATE_COURSE("update_student_courses");

        private TASKS(final String text) {
            this.text = text;
        }

        private final String text;
        @Override
        public String toString() {
            return text;
        }
    }

    // Required for callback of the requesting activity
    void hasResult( PWApi.TASKS task, String result );
}