/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function ($) {
    const hash = window.location.hash;
    debugger;
    if (hash) {
        setTimeout(function(){
            $('.nav-tabs a[href="${hash}"]').tab("show"); }, 500);

    } else {
        setTimeout(function(){
            $("#tabList").find('a:first').tab("show"); }, 500);
    }

    $('.nav-tabs a').click(function () {
        $(this).tab('show');
        const scrollMem = $('body').scrollTop() || $('html').scrollTop();
        window.location.hash = this.hash;
        $('html,body').scrollTop(scrollMem);
    });
})
(jQuery3);
