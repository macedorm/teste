package br.com.jjconsulting.mobile.jjlib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.jjconsulting.mobile.jjlib.dao.entity.DataItemValue;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;

public class IconHelper {
    private static HashMap<Integer, String> _icons;
    private static HashMap<Integer, String> Icons;

    public static HashMap<Integer, String> getIcons() {

        if (_icons != null)
            return _icons;

        _icons = new HashMap<>();
        _icons.put(0, "fa_adjust");
        _icons.put(1, "fa_adn");
        _icons.put(2, "fa_align_center");
        _icons.put(3, "fa_align_justify");
        _icons.put(4, "fa_align_left");
        _icons.put(5, "fa_align_right");
        _icons.put(6, "fa_amazon");
        _icons.put(7, "fa_ambulance");
        _icons.put(8, "fa_anchor");
        _icons.put(9, "fa_android");
        _icons.put(10, "fa_angellist");
        _icons.put(11, "fa_angle_double_down");
        _icons.put(12, "fa_angle_double_left");
        _icons.put(13, "fa_angle_double_right");
        _icons.put(14, "fa_angle_double_up");
        _icons.put(15, "fa_angle_down");
        _icons.put(16, "fa_angle_left");
        _icons.put(17, "fa_angle_right");
        _icons.put(18, "fa_angle_up");
        _icons.put(19, "fa_apple");
        _icons.put(20, "fa_archive");
        _icons.put(21, "fa_area_chart");
        _icons.put(22, "fa_arrow_circle_down");
        _icons.put(23, "fa_arrow_circle_left");
        _icons.put(24, "fa_arrow_circle_o_down");
        _icons.put(25, "fa_arrow_circle_o_left");
        _icons.put(26, "fa_arrow_circle_o_right");
        _icons.put(27, "fa_arrow_circle_o_up");
        _icons.put(28, "fa_arrow_circle_right");
        _icons.put(29, "fa_arrow_circle_up");
        _icons.put(30, "fa_arrow_down");
        _icons.put(31, "fa_arrow_left");
        _icons.put(32, "fa_arrow_right");
        _icons.put(33, "fa_arrow_up");
        _icons.put(34, "fa_arrows");
        _icons.put(35, "fa_arrows_alt");
        _icons.put(36, "fa_arrows_h");
        _icons.put(37, "fa_arrows_v");
        _icons.put(38, "fa_asterisk");
        _icons.put(39, "fa_at");
        _icons.put(40, "fa_automobile");
        _icons.put(41, "fa_backward");
        _icons.put(42, "fa_balance_scale");
        _icons.put(43, "fa_ban");
        _icons.put(44, "fa_bank");
        _icons.put(45, "fa_bar_chart");
        _icons.put(46, "fa_bar_chart_o");
        _icons.put(47, "fa_barcode");
        _icons.put(48, "fa_bars");
        _icons.put(49, "fa_battery_0");
        _icons.put(50, "fa_battery_1");
        _icons.put(51, "fa_battery_2");
        _icons.put(52, "fa_battery_3");
        _icons.put(53, "fa_battery_4");
        _icons.put(54, "fa_battery_empty");
        _icons.put(55, "fa_battery_full");
        _icons.put(56, "fa_battery_half");
        _icons.put(57, "fa_battery_quarter");
        _icons.put(58, "fa_battery_three_quarters");
        _icons.put(59, "fa_bed");
        _icons.put(60, "fa_beer");
        _icons.put(61, "fa_behance");
        _icons.put(62, "fa_behance_square");
        _icons.put(63, "fa_bell");
        _icons.put(64, "fa_bell_o");
        _icons.put(65, "fa_bell_slash");
        _icons.put(66, "fa_bell_slash_o");
        _icons.put(67, "fa_bicycle");
        _icons.put(68, "fa_binoculars");
        _icons.put(69, "fa_birthday_cake");
        _icons.put(70, "fa_bitbucket");
        _icons.put(71, "fa_bitbucket_square");
        _icons.put(72, "fa_bitcoin");
        _icons.put(73, "fa_black_tie");
        _icons.put(74, "fa_bluetooth");
        _icons.put(75, "fa_bluetooth_b");
        _icons.put(76, "fa_bold");
        _icons.put(77, "fa_bolt");
        _icons.put(78, "fa_bomb");
        _icons.put(79, "fa_book");
        _icons.put(80, "fa_bookmark");
        _icons.put(81, "fa_bookmark_o");
        _icons.put(82, "fa_briefcase");
        _icons.put(83, "fa_btc");
        _icons.put(84, "fa_bug");
        _icons.put(85, "fa_building");
        _icons.put(86, "fa_building_o");
        _icons.put(87, "fa_bullhorn");
        _icons.put(88, "fa_bullseye");
        _icons.put(89, "fa_bus");
        _icons.put(90, "fa_buysellads");
        _icons.put(91, "fa_cab");
        _icons.put(92, "fa_calculator");
        _icons.put(93, "fa_calendar");
        _icons.put(94, "fa_calendar_check_o");
        _icons.put(95, "fa_calendar_minus_o");
        _icons.put(96, "fa_calendar_o");
        _icons.put(97, "fa_calendar_plus_o");
        _icons.put(98, "fa_calendar_times_o");
        _icons.put(99, "fa_camera");
        _icons.put(100, "fa_camera_retro");
        _icons.put(101, "fa_car");
        _icons.put(102, "fa_caret_down");
        _icons.put(103, "fa_caret_left");
        _icons.put(104, "fa_caret_right");
        _icons.put(105, "fa_caret_square_o_down");
        _icons.put(106, "fa_caret_square_o_left");
        _icons.put(107, "fa_caret_square_o_right");
        _icons.put(108, "fa_caret_square_o_up");
        _icons.put(109, "fa_caret_up");
        _icons.put(110, "fa_cart_arrow_down");
        _icons.put(111, "fa_cart_plus");
        _icons.put(112, "fa_cc");
        _icons.put(113, "fa_cc_amex");
        _icons.put(114, "fa_cc_diners_club");
        _icons.put(115, "fa_cc_discover");
        _icons.put(116, "fa_cc_jcb");
        _icons.put(117, "fa_cc_mastercard");
        _icons.put(118, "fa_cc_paypal");
        _icons.put(119, "fa_cc_stripe");
        _icons.put(120, "fa_cc_visa");
        _icons.put(121, "fa_certificate");
        _icons.put(122, "fa_chain");
        _icons.put(123, "fa_chain_broken");
        _icons.put(124, "fa_check");
        _icons.put(125, "fa_check_circle");
        _icons.put(126, "fa_check_circle_o");
        _icons.put(127, "fa_check_square");
        _icons.put(128, "fa_check_square_o");
        _icons.put(129, "fa_chevron_circle_down");
        _icons.put(130, "fa_chevron_circle_left");
        _icons.put(131, "fa_chevron_circle_right");
        _icons.put(132, "fa_chevron_circle_up");
        _icons.put(133, "fa_chevron_down");
        _icons.put(134, "fa_chevron_left");
        _icons.put(135, "fa_chevron_right");
        _icons.put(136, "fa_chevron_up");
        _icons.put(137, "fa_child");
        _icons.put(138, "fa_chrome");
        _icons.put(139, "fa_circle");
        _icons.put(140, "fa_circle_o");
        _icons.put(141, "fa_circle_o_notch");
        _icons.put(142, "fa_circle_thin");
        _icons.put(143, "fa_clipboard");
        _icons.put(144, "fa_clock_o");
        _icons.put(145, "fa_clone");
        _icons.put(146, "fa_close");
        _icons.put(147, "fa_cloud");
        _icons.put(148, "fa_cloud_download");
        _icons.put(149, "fa_cloud_upload");
        _icons.put(150, "fa_cny");
        _icons.put(151, "fa_code");
        _icons.put(152, "fa_code_fork");
        _icons.put(153, "fa_codepen");
        _icons.put(154, "fa_codiepie");
        _icons.put(155, "fa_coffee");
        _icons.put(156, "fa_cog");
        _icons.put(157, "fa_cogs");
        _icons.put(158, "fa_columns");
        _icons.put(159, "fa_comment");
        _icons.put(160, "fa_comment_o");
        _icons.put(161, "fa_commenting");
        _icons.put(162, "fa_commenting_o");
        _icons.put(163, "fa_comments");
        _icons.put(164, "fa_comments_o");
        _icons.put(165, "fa_compass");
        _icons.put(166, "fa_compress");
        _icons.put(167, "fa_connectdevelop");
        _icons.put(168, "fa_contao");
        _icons.put(169, "fa_copy");
        _icons.put(170, "fa_copyright");
        _icons.put(171, "fa_creative_commons");
        _icons.put(172, "fa_credit_card");
        _icons.put(173, "fa_credit_card_alt");
        _icons.put(174, "fa_crop");
        _icons.put(175, "fa_crosshairs");
        _icons.put(176, "fa_css3");
        _icons.put(177, "fa_cube");
        _icons.put(178, "fa_cubes");
        _icons.put(179, "fa_cut");
        _icons.put(180, "fa_cutlery");
        _icons.put(181, "fa_dashboard");
        _icons.put(182, "fa_dashcube");
        _icons.put(183, "fa_database");
        _icons.put(184, "fa_dedent");
        _icons.put(185, "fa_delicious");
        _icons.put(186, "fa_desktop");
        _icons.put(187, "fa_deviantart");
        _icons.put(188, "fa_diamond");
        _icons.put(189, "fa_digg");
        _icons.put(190, "fa_dollar");
        _icons.put(191, "fa_dot_circle_o");
        _icons.put(192, "fa_download");
        _icons.put(193, "fa_dribbble");
        _icons.put(194, "fa_dropbox");
        _icons.put(195, "fa_drupal");
        _icons.put(196, "fa_edge");
        _icons.put(197, "fa_edit");
        _icons.put(198, "fa_eject");
        _icons.put(199, "fa_ellipsis_h");
        _icons.put(200, "fa_ellipsis_v");
        _icons.put(201, "fa_empire");
        _icons.put(202, "fa_envelope");
        _icons.put(203, "fa_envelope_o");
        _icons.put(204, "fa_envelope_square");
        _icons.put(205, "fa_eraser");
        _icons.put(206, "fa_eur");
        _icons.put(207, "fa_euro");
        _icons.put(208, "fa_exchange");
        _icons.put(209, "fa_exclamation");
        _icons.put(210, "fa_exclamation_circle");
        _icons.put(211, "fa_exclamation_triangle");
        _icons.put(212, "fa_expand");
        _icons.put(213, "fa_expeditedssl");
        _icons.put(214, "fa_external_link");
        _icons.put(215, "fa_external_link_square");
        _icons.put(216, "fa_eye");
        _icons.put(217, "fa_eye_slash");
        _icons.put(218, "fa_eyedropper");
        _icons.put(219, "fa_facebook");
        _icons.put(220, "fa_facebook_f");
        _icons.put(221, "fa_facebook_official");
        _icons.put(222, "fa_facebook_square");
        _icons.put(223, "fa_fast_backward");
        _icons.put(224, "fa_fast_forward");
        _icons.put(225, "fa_fax");
        _icons.put(226, "fa_feed");
        _icons.put(227, "fa_female");
        _icons.put(228, "fa_fighter_jet");
        _icons.put(229, "fa_file");
        _icons.put(230, "fa_file_archive_o");
        _icons.put(231, "fa_file_audio_o");
        _icons.put(232, "fa_file_code_o");
        _icons.put(233, "fa_file_excel_o");
        _icons.put(234, "fa_file_image_o");
        _icons.put(235, "fa_file_movie_o");
        _icons.put(236, "fa_file_o");
        _icons.put(237, "fa_file_pdf_o");
        _icons.put(238, "fa_file_photo_o");
        _icons.put(239, "fa_file_picture_o");
        _icons.put(240, "fa_file_powerpoint_o");
        _icons.put(241, "fa_file_sound_o");
        _icons.put(242, "fa_file_text");
        _icons.put(243, "fa_file_text_o");
        _icons.put(244, "fa_file_video_o");
        _icons.put(245, "fa_file_word_o");
        _icons.put(246, "fa_file_zip_o");
        _icons.put(247, "fa_files_o");
        _icons.put(248, "fa_film");
        _icons.put(249, "fa_filter");
        _icons.put(250, "fa_fire");
        _icons.put(251, "fa_fire_extinguisher");
        _icons.put(252, "fa_firefox");
        _icons.put(253, "fa_flag");
        _icons.put(254, "fa_flag_checkered");
        _icons.put(255, "fa_flag_o");
        _icons.put(256, "fa_flash");
        _icons.put(257, "fa_flask");
        _icons.put(258, "fa_flickr");
        _icons.put(259, "fa_floppy_o");
        _icons.put(260, "fa_folder");
        _icons.put(261, "fa_folder_o");
        _icons.put(262, "fa_folder_open");
        _icons.put(263, "fa_folder_open_o");
        _icons.put(264, "fa_font");
        _icons.put(265, "fa_fonticons");
        _icons.put(266, "fa_fort_awesome");
        _icons.put(267, "fa_forumbee");
        _icons.put(268, "fa_forward");
        _icons.put(269, "fa_foursquare");
        _icons.put(270, "fa_frown_o");
        _icons.put(271, "fa_futbol_o");
        _icons.put(272, "fa_gamepad");
        _icons.put(273, "fa_gavel");
        _icons.put(274, "fa_gbp");
        _icons.put(275, "fa_ge");
        _icons.put(276, "fa_gear");
        _icons.put(277, "fa_gears");
        _icons.put(278, "fa_genderless");
        _icons.put(279, "fa_get_pocket");
        _icons.put(280, "fa_gg");
        _icons.put(281, "fa_gg_circle");
        _icons.put(282, "fa_gift");
        _icons.put(283, "fa_git");
        _icons.put(284, "fa_git_square");
        _icons.put(285, "fa_github");
        _icons.put(286, "fa_github_alt");
        _icons.put(287, "fa_github_square");
        _icons.put(288, "fa_gittip");
        _icons.put(289, "fa_glass");
        _icons.put(290, "fa_globe");
        _icons.put(291, "fa_google");
        _icons.put(292, "fa_google_plus");
        _icons.put(293, "fa_google_plus_square");
        _icons.put(294, "fa_google_wallet");
        _icons.put(295, "fa_graduation_cap");
        _icons.put(296, "fa_gratipay");
        _icons.put(297, "fa_group");
        _icons.put(298, "fa_h_square");
        _icons.put(299, "fa_hacker_news");
        _icons.put(300, "fa_hand_grab_o");
        _icons.put(301, "fa_hand_lizard_o");
        _icons.put(302, "fa_hand_o_down");
        _icons.put(303, "fa_hand_o_left");
        _icons.put(304, "fa_hand_o_right");
        _icons.put(305, "fa_hand_o_up");
        _icons.put(306, "fa_hand_paper_o");
        _icons.put(307, "fa_hand_peace_o");
        _icons.put(308, "fa_hand_pointer_o");
        _icons.put(309, "fa_hand_rock_o");
        _icons.put(310, "fa_hand_scissors_o");
        _icons.put(311, "fa_hand_spock_o");
        _icons.put(312, "fa_hand_stop_o");
        _icons.put(313, "fa_hashtag");
        _icons.put(314, "fa_hdd_o");
        _icons.put(315, "fa_header");
        _icons.put(316, "fa_headphones");
        _icons.put(317, "fa_heart");
        _icons.put(318, "fa_heart_o");
        _icons.put(319, "fa_heartbeat");
        _icons.put(320, "fa_history");
        _icons.put(321, "fa_home");
        _icons.put(322, "fa_hospital_o");
        _icons.put(323, "fa_hotel");
        _icons.put(324, "fa_hourglass");
        _icons.put(325, "fa_hourglass_1");
        _icons.put(326, "fa_hourglass_2");
        _icons.put(327, "fa_hourglass_3");
        _icons.put(328, "fa_hourglass_end");
        _icons.put(329, "fa_hourglass_half");
        _icons.put(330, "fa_hourglass_o");
        _icons.put(331, "fa_hourglass_start");
        _icons.put(332, "fa_houzz");
        _icons.put(333, "fa_html5");
        _icons.put(334, "fa_i_cursor");
        _icons.put(335, "fa_ils");
        _icons.put(336, "fa_image");
        _icons.put(337, "fa_inbox");
        _icons.put(338, "fa_indent");
        _icons.put(339, "fa_industry");
        _icons.put(340, "fa_info");
        _icons.put(341, "fa_info_circle");
        _icons.put(342, "fa_inr");
        _icons.put(343, "fa_instagram");
        _icons.put(344, "fa_institution");
        _icons.put(345, "fa_internet_explorer");
        _icons.put(346, "fa_intersex");
        _icons.put(347, "fa_ioxhost");
        _icons.put(348, "fa_italic");
        _icons.put(349, "fa_joomla");
        _icons.put(350, "fa_jpy");
        _icons.put(351, "fa_jsfiddle");
        _icons.put(352, "fa_key");
        _icons.put(353, "fa_keyboard_o");
        _icons.put(354, "fa_krw");
        _icons.put(355, "fa_language");
        _icons.put(356, "fa_laptop");
        _icons.put(357, "fa_lastfm");
        _icons.put(358, "fa_lastfm_square");
        _icons.put(359, "fa_leaf");
        _icons.put(360, "fa_leanpub");
        _icons.put(361, "fa_legal");
        _icons.put(362, "fa_lemon_o");
        _icons.put(363, "fa_level_down");
        _icons.put(364, "fa_level_up");
        _icons.put(365, "fa_life_bouy");
        _icons.put(366, "fa_life_buoy");
        _icons.put(367, "fa_life_ring");
        _icons.put(368, "fa_life_saver");
        _icons.put(369, "fa_lightbulb_o");
        _icons.put(370, "fa_line_chart");
        _icons.put(371, "fa_link");
        _icons.put(372, "fa_linkedin");
        _icons.put(373, "fa_linkedin_square");
        _icons.put(374, "fa_linux");
        _icons.put(375, "fa_list");
        _icons.put(376, "fa_list_alt");
        _icons.put(377, "fa_list_ol");
        _icons.put(378, "fa_list_ul");
        _icons.put(379, "fa_location_arrow");
        _icons.put(380, "fa_lock");
        _icons.put(381, "fa_long_arrow_down");
        _icons.put(382, "fa_long_arrow_left");
        _icons.put(383, "fa_long_arrow_right");
        _icons.put(384, "fa_long_arrow_up");
        _icons.put(385, "fa_magic");
        _icons.put(386, "fa_magnet");
        _icons.put(387, "fa_mail_forward");
        _icons.put(388, "fa_mail_reply");
        _icons.put(389, "fa_mail_reply_all");
        _icons.put(390, "fa_male");
        _icons.put(391, "fa_map");
        _icons.put(392, "fa_map_marker");
        _icons.put(393, "fa_map_o");
        _icons.put(394, "fa_map_pin");
        _icons.put(395, "fa_map_signs");
        _icons.put(396, "fa_mars");
        _icons.put(397, "fa_mars_double");
        _icons.put(398, "fa_mars_stroke");
        _icons.put(399, "fa_mars_stroke_h");
        _icons.put(400, "fa_mars_stroke_v");
        _icons.put(401, "fa_maxcdn");
        _icons.put(402, "fa_meanpath");
        _icons.put(403, "fa_medium");
        _icons.put(404, "fa_medkit");
        _icons.put(405, "fa_meh_o");
        _icons.put(406, "fa_mercury");
        _icons.put(407, "fa_microphone");
        _icons.put(408, "fa_microphone_slash");
        _icons.put(409, "fa_minus");
        _icons.put(410, "fa_minus_circle");
        _icons.put(411, "fa_minus_square");
        _icons.put(412, "fa_minus_square_o");
        _icons.put(413, "fa_mixcloud");
        _icons.put(414, "fa_mobile");
        _icons.put(415, "fa_mobile_phone");
        _icons.put(416, "fa_modx");
        _icons.put(417, "fa_money");
        _icons.put(418, "fa_moon_o");
        _icons.put(419, "fa_mortar_board");
        _icons.put(420, "fa_motorcycle");
        _icons.put(421, "fa_mouse_pointer");
        _icons.put(422, "fa_music");
        _icons.put(423, "fa_navicon");
        _icons.put(424, "fa_neuter");
        _icons.put(425, "fa_newspaper_o");
        _icons.put(426, "fa_object_group");
        _icons.put(427, "fa_object_ungroup");
        _icons.put(428, "fa_odnoklassniki");
        _icons.put(429, "fa_odnoklassniki_square");
        _icons.put(430, "fa_opencart");
        _icons.put(431, "fa_openid");
        _icons.put(432, "fa_opera");
        _icons.put(433, "fa_optin_monster");
        _icons.put(434, "fa_outdent");
        _icons.put(435, "fa_pagelines");
        _icons.put(436, "fa_paint_brush");
        _icons.put(437, "fa_paper_plane");
        _icons.put(438, "fa_paper_plane_o");
        _icons.put(439, "fa_paperclip");
        _icons.put(440, "fa_paragraph");
        _icons.put(441, "fa_paste");
        _icons.put(442, "fa_pause");
        _icons.put(443, "fa_pause_circle");
        _icons.put(444, "fa_pause_circle_o");
        _icons.put(445, "fa_paw");
        _icons.put(446, "fa_paypal");
        _icons.put(447, "fa_pencil");
        _icons.put(448, "fa_pencil_square");
        _icons.put(449, "fa_pencil_square_o");
        _icons.put(450, "fa_percent");
        _icons.put(451, "fa_phone");
        _icons.put(452, "fa_phone_square");
        _icons.put(453, "fa_photo");
        _icons.put(454, "fa_picture_o");
        _icons.put(455, "fa_pie_chart");
        _icons.put(456, "fa_pied_piper");
        _icons.put(457, "fa_pied_piper_alt");
        _icons.put(458, "fa_pinterest");
        _icons.put(459, "fa_pinterest_p");
        _icons.put(460, "fa_pinterest_square");
        _icons.put(461, "fa_plane");
        _icons.put(462, "fa_play");
        _icons.put(463, "fa_play_circle");
        _icons.put(464, "fa_play_circle_o");
        _icons.put(465, "fa_plug");
        _icons.put(466, "fa_plus");
        _icons.put(467, "fa_plus_circle");
        _icons.put(468, "fa_plus_square");
        _icons.put(469, "fa_plus_square_o");
        _icons.put(470, "fa_power_off");
        _icons.put(471, "fa_print");
        _icons.put(472, "fa_product_hunt");
        _icons.put(473, "fa_puzzle_piece");
        _icons.put(474, "fa_qq");
        _icons.put(475, "fa_qrcode");
        _icons.put(476, "fa_question");
        _icons.put(477, "fa_question_circle");
        _icons.put(478, "fa_quote_left");
        _icons.put(479, "fa_quote_right");
        _icons.put(480, "fa_ra");
        _icons.put(481, "fa_random");
        _icons.put(482, "fa_rebel");
        _icons.put(483, "fa_recycle");
        _icons.put(484, "fa_reddit");
        _icons.put(485, "fa_reddit_alien");
        _icons.put(486, "fa_reddit_square");
        _icons.put(487, "fa_refresh");
        _icons.put(488, "fa_registered");
        _icons.put(489, "fa_remove");
        _icons.put(490, "fa_renren");
        _icons.put(491, "fa_reorder");
        _icons.put(492, "fa_repeat");
        _icons.put(493, "fa_reply");
        _icons.put(494, "fa_reply_all");
        _icons.put(495, "fa_retweet");
        _icons.put(496, "fa_rmb");
        _icons.put(497, "fa_road");
        _icons.put(498, "fa_rocket");
        _icons.put(499, "fa_rotate_left");
        _icons.put(500, "fa_rotate_right");
        _icons.put(501, "fa_rouble");
        _icons.put(502, "fa_rss");
        _icons.put(503, "fa_rss_square");
        _icons.put(504, "fa_rub");
        _icons.put(505, "fa_ruble");
        _icons.put(506, "fa_rupee");
        _icons.put(507, "fa_safari");
        _icons.put(508, "fa_save");
        _icons.put(509, "fa_scissors");
        _icons.put(510, "fa_scribd");
        _icons.put(511, "fa_search");
        _icons.put(512, "fa_search_minus");
        _icons.put(513, "fa_search_plus");
        _icons.put(514, "fa_sellsy");
        _icons.put(515, "fa_send");
        _icons.put(516, "fa_send_o");
        _icons.put(517, "fa_server");
        _icons.put(518, "fa_share");
        _icons.put(519, "fa_share_alt");
        _icons.put(520, "fa_share_alt_square");
        _icons.put(521, "fa_share_square");
        _icons.put(522, "fa_share_square_o");
        _icons.put(523, "fa_shekel");
        _icons.put(524, "fa_sheqel");
        _icons.put(525, "fa_shield");
        _icons.put(526, "fa_ship");
        _icons.put(527, "fa_shirtsinbulk");
        _icons.put(528, "fa_shopping_bag");
        _icons.put(529, "fa_shopping_basket");
        _icons.put(530, "fa_shopping_cart");
        _icons.put(531, "fa_sign_in");
        _icons.put(532, "fa_sign_out");
        _icons.put(533, "fa_signal");
        _icons.put(534, "fa_simplybuilt");
        _icons.put(535, "fa_sitemap");
        _icons.put(536, "fa_skyatlas");
        _icons.put(537, "fa_skype");
        _icons.put(538, "fa_slack");
        _icons.put(539, "fa_sliders");
        _icons.put(540, "fa_slideshare");
        _icons.put(541, "fa_smile_o");
        _icons.put(542, "fa_soccer_ball_o");
        _icons.put(543, "fa_sort");
        _icons.put(544, "fa_sort_alpha_asc");
        _icons.put(545, "fa_sort_alpha_desc");
        _icons.put(546, "fa_sort_amount_asc");
        _icons.put(547, "fa_sort_amount_desc");
        _icons.put(548, "fa_sort_asc");
        _icons.put(549, "fa_sort_desc");
        _icons.put(550, "fa_sort_down");
        _icons.put(551, "fa_sort_numeric_asc");
        _icons.put(552, "fa_sort_numeric_desc");
        _icons.put(553, "fa_sort_up");
        _icons.put(554, "fa_soundcloud");
        _icons.put(555, "fa_space_shuttle");
        _icons.put(556, "fa_spinner");
        _icons.put(557, "fa_spoon");
        _icons.put(558, "fa_spotify");
        _icons.put(559, "fa_square");
        _icons.put(560, "fa_square_o");
        _icons.put(561, "fa_stack_exchange");
        _icons.put(562, "fa_stack_overflow");
        _icons.put(563, "fa_star");
        _icons.put(564, "fa_star_half");
        _icons.put(565, "fa_star_half_empty");
        _icons.put(566, "fa_star_half_full");
        _icons.put(567, "fa_star_half_o");
        _icons.put(568, "fa_star_o");
        _icons.put(569, "fa_steam");
        _icons.put(570, "fa_steam_square");
        _icons.put(571, "fa_step_backward");
        _icons.put(572, "fa_step_forward");
        _icons.put(573, "fa_stethoscope");
        _icons.put(574, "fa_sticky_note");
        _icons.put(575, "fa_sticky_note_o");
        _icons.put(576, "fa_stop");
        _icons.put(577, "fa_stop_circle");
        _icons.put(578, "fa_stop_circle_o");
        _icons.put(579, "fa_street_view");
        _icons.put(580, "fa_strikethrough");
        _icons.put(581, "fa_stumbleupon");
        _icons.put(582, "fa_stumbleupon_circle");
        _icons.put(583, "fa_subscript");
        _icons.put(584, "fa_subway");
        _icons.put(585, "fa_suitcase");
        _icons.put(586, "fa_sun_o");
        _icons.put(587, "fa_superscript");
        _icons.put(588, "fa_support");
        _icons.put(589, "fa_table");
        _icons.put(590, "fa_tablet");
        _icons.put(591, "fa_tachometer");
        _icons.put(592, "fa_tag");
        _icons.put(593, "fa_tags");
        _icons.put(594, "fa_tasks");
        _icons.put(595, "fa_taxi");
        _icons.put(596, "fa_television");
        _icons.put(597, "fa_tencent_weibo");
        _icons.put(598, "fa_terminal");
        _icons.put(599, "fa_text_height");
        _icons.put(600, "fa_text_width");
        _icons.put(601, "fa_th");
        _icons.put(602, "fa_th_large");
        _icons.put(603, "fa_th_list");
        _icons.put(604, "fa_thumb_tack");
        _icons.put(605, "fa_thumbs_down");
        _icons.put(606, "fa_thumbs_o_down");
        _icons.put(607, "fa_thumbs_o_up");
        _icons.put(608, "fa_thumbs_up");
        _icons.put(609, "fa_ticket");
        _icons.put(610, "fa_times");
        _icons.put(611, "fa_times_circle");
        _icons.put(612, "fa_times_circle_o");
        _icons.put(613, "fa_tint");
        _icons.put(614, "fa_toggle_down");
        _icons.put(615, "fa_toggle_left");
        _icons.put(616, "fa_toggle_off");
        _icons.put(617, "fa_toggle_on");
        _icons.put(618, "fa_toggle_right");
        _icons.put(619, "fa_toggle_up");
        _icons.put(620, "fa_trademark");
        _icons.put(621, "fa_train");
        _icons.put(622, "fa_transgender");
        _icons.put(623, "fa_transgender_alt");
        _icons.put(624, "fa_trash");
        _icons.put(625, "fa_trash_o");
        _icons.put(626, "fa_tree");
        _icons.put(627, "fa_trello");
        _icons.put(628, "fa_tripadvisor");
        _icons.put(629, "fa_trophy");
        _icons.put(630, "fa_truck");
        _icons.put(631, "fa_try");
        _icons.put(632, "fa_tty");
        _icons.put(633, "fa_tumblr");
        _icons.put(634, "fa_tumblr_square");
        _icons.put(635, "fa_turkish_lira");
        _icons.put(636, "fa_tv");
        _icons.put(637, "fa_twitch");
        _icons.put(638, "fa_twitter");
        _icons.put(639, "fa_twitter_square");
        _icons.put(640, "fa_umbrella");
        _icons.put(641, "fa_underline");
        _icons.put(642, "fa_undo");
        _icons.put(643, "fa_university");
        _icons.put(644, "fa_unlink");
        _icons.put(645, "fa_unlock");
        _icons.put(646, "fa_unlock_alt");
        _icons.put(647, "fa_unsorted");
        _icons.put(648, "fa_upload");
        _icons.put(649, "fa_usb");
        _icons.put(650, "fa_usd");
        _icons.put(651, "fa_user");
        _icons.put(652, "fa_user_md");
        _icons.put(653, "fa_user_plus");
        _icons.put(654, "fa_user_secret");
        _icons.put(655, "fa_user_times");
        _icons.put(656, "fa_users");
        _icons.put(657, "fa_venus");
        _icons.put(658, "fa_venus_double");
        _icons.put(659, "fa_venus_mars");
        _icons.put(660, "fa_viacoin");
        _icons.put(661, "fa_video_camera");
        _icons.put(662, "fa_vimeo");
        _icons.put(663, "fa_vimeo_square");
        _icons.put(664, "fa_vine");
        _icons.put(665, "fa_vk");
        _icons.put(666, "fa_volume_down");
        _icons.put(667, "fa_volume_off");
        _icons.put(668, "fa_volume_up");
        _icons.put(669, "fa_warning");
        _icons.put(670, "fa_wechat");
        _icons.put(671, "fa_weibo");
        _icons.put(672, "fa_weixin");
        _icons.put(673, "fa_whatsapp");
        _icons.put(674, "fa_wheelchair");
        _icons.put(675, "fa_wifi");
        _icons.put(676, "fa_wikipedia_w");
        _icons.put(677, "fa_windows");
        _icons.put(678, "fa_won");
        _icons.put(679, "fa_wordpress");
        _icons.put(680, "fa_wrench");
        _icons.put(681, "fa_xing");
        _icons.put(682, "fa_xing_square");
        _icons.put(683, "fa_y_combinator");
        _icons.put(684, "fa_y_combinator_square");
        _icons.put(685, "fa_yahoo");
        _icons.put(686, "fa_yc");
        _icons.put(687, "fa_yc_square");
        _icons.put(688, "fa_yelp");
        _icons.put(689, "fa_yen");
        _icons.put(690, "fa_youtube");
        _icons.put(691, "fa_youtube_play");
        _icons.put(692, "fa_youtube_square");
        return _icons;
    }

    public static void setIcons(HashMap<Integer, String> _icons) {
        IconHelper._icons = _icons;
    }

    public static ArrayList<DataItemValue> getListImage()
    {
        ArrayList<DataItemValue> images = new ArrayList();

        for (Map.Entry<Integer, String> pair : getIcons().entrySet()) {

            TIcon icon = (TIcon.values()[pair.getKey()]);

            String id = pair.getKey() + "";
            String name = pair.getValue();
            String description = icon.toString();
            images.add(new DataItemValue(id, description, icon.getValue(), "#000000"));
        }

        return images;
    }

    public static String getName(TIcon icon)
    {
        return getIcons().get(icon.getValue());
    }

}
