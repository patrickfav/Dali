/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


#include "ip.rsh"

static float bright = 0.f;

void setBright(float v) {
    bright = v / 100.f *255;
}

void brightness(const uchar4 *in, uchar4 *out)
{
    out->r = rsClamp((int)(in->r + bright), 0, 255);
    out->g = rsClamp((int)(in->g + bright), 0, 255);
    out->b = rsClamp((int)(in->b + bright), 0, 255);
}