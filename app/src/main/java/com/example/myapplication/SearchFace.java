package com.example.myapplication;

import java.util.List;

public class SearchFace {
    /**
     * error_code : 0
     * error_msg : SUCCESS
     * log_id : 305486804170406081
     * timestamp : 1550417040
     * cached : 0
     * result : {"face_num":1,"face_list":[{"face_token":"a2be55b56c274334b8ad655b75752520","location":{"left":19.66,"top":111.39,"width":82,"height":85,"rotation":2},"face_probability":1,"angle":{"yaw":-5.15,"pitch":6.1,"roll":1.32},"face_shape":{"type":"oval","probability":0.46},"face_type":{"type":"human","probability":1}}]}
     */

    private int error_code;
    private String error_msg;
    private long log_id;
    private int timestamp;
    private int cached;
    private ResultBean result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getCached() {
        return cached;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * face_num : 1
         * face_list : [{"face_token":"a2be55b56c274334b8ad655b75752520","location":{"left":19.66,"top":111.39,"width":82,"height":85,"rotation":2},"face_probability":1,"angle":{"yaw":-5.15,"pitch":6.1,"roll":1.32},"face_shape":{"type":"oval","probability":0.46},"face_type":{"type":"human","probability":1}}]
         */

        private int face_num;
        private List<FaceListBean> face_list;

        public int getFace_num() {
            return face_num;
        }

        public void setFace_num(int face_num) {
            this.face_num = face_num;
        }

        public List<FaceListBean> getFace_list() {
            return face_list;
        }

        public void setFace_list(List<FaceListBean> face_list) {
            this.face_list = face_list;
        }

        public static class FaceListBean {
            /**
             * face_token : a2be55b56c274334b8ad655b75752520
             * location : {"left":19.66,"top":111.39,"width":82,"height":85,"rotation":2}
             * face_probability : 1
             * angle : {"yaw":-5.15,"pitch":6.1,"roll":1.32}
             * face_shape : {"type":"oval","probability":0.46}
             * face_type : {"type":"human","probability":1}
             */

            private String face_token;
            private LocationBean location;
            private float face_probability;
            private AngleBean angle;
            private FaceShapeBean face_shape;
            private FaceTypeBean face_type;

            public String getFace_token() {
                return face_token;
            }

            public void setFace_token(String face_token) {
                this.face_token = face_token;
            }

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public float getFace_probability() {
                return face_probability;
            }

            public void setFace_probability(float face_probability) {
                this.face_probability = face_probability;
            }

            public AngleBean getAngle() {
                return angle;
            }

            public void setAngle(AngleBean angle) {
                this.angle = angle;
            }

            public FaceShapeBean getFace_shape() {
                return face_shape;
            }

            public void setFace_shape(FaceShapeBean face_shape) {
                this.face_shape = face_shape;
            }

            public FaceTypeBean getFace_type() {
                return face_type;
            }

            public void setFace_type(FaceTypeBean face_type) {
                this.face_type = face_type;
            }

            public static class LocationBean {
                /**
                 * left : 19.66
                 * top : 111.39
                 * width : 82
                 * height : 85
                 * rotation : 2
                 */

                private float left;
                private float top;
                private float width;
                private float height;
                private int rotation;

                public float getLeft() {
                    return left;
                }

                public void setLeft(float left) {
                    this.left = left;
                }

                public float getTop() {
                    return top;
                }

                public void setTop(float top) {
                    this.top = top;
                }

                public float getWidth() {
                    return width;
                }

                public void setWidth(float width) {
                    this.width = width;
                }

                public float getHeight() {
                    return height;
                }

                public void setHeight(float height) {
                    this.height = height;
                }

                public int getRotation() {
                    return rotation;
                }

                public void setRotation(int rotation) {
                    this.rotation = rotation;
                }
            }

            public static class AngleBean {
                /**
                 * yaw : -5.15
                 * pitch : 6.1
                 * roll : 1.32
                 */

                private float yaw;
                private float pitch;
                private float roll;

                public float getYaw() {
                    return yaw;
                }

                public void setYaw(float yaw) {
                    this.yaw = yaw;
                }

                public float getPitch() {
                    return pitch;
                }

                public void setPitch(float pitch) {
                    this.pitch = pitch;
                }

                public float getRoll() {
                    return roll;
                }

                public void setRoll(float roll) {
                    this.roll = roll;
                }
            }

            public static class FaceShapeBean {
                /**
                 * type : oval
                 * probability : 0.46
                 */

                private String type;
                private float probability;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public float getProbability() {
                    return probability;
                }

                public void setProbability(float probability) {
                    this.probability = probability;
                }
            }

            public static class FaceTypeBean {
                /**
                 * type : human
                 * probability : 1
                 */

                private String type;
                private int probability;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public int getProbability() {
                    return probability;
                }

                public void setProbability(int probability) {
                    this.probability = probability;
                }
            }
        }
    }

}
