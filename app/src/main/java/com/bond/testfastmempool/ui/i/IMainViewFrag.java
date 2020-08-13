package com.bond.testfastmempool.ui.i;

import android.content.Intent;
import android.view.View;

/**
 * Интерфейс фрагмента, который может быть инжектирован
 * в качестве основного окна на весь экран
 */
public interface IMainViewFrag {
  /**
   * Получить вьюху для отрисовки на экране
   * @return
   */
  View getMainView();

  /**
   * Синхронизация с жизненным циклом:
   */
  void  onStartMainView();
  void  onStopMainView();

  /**
   * Проброс onActivityResult
   * Если текущее главное окно заказывало ЭТО
   * то должно вернуть true - иначе обработка уйдёт в super
   * @param requestCode
   * @param resultCode
   * @param data
   * @return
   */
  boolean  onActivityResultMainView(int requestCode,
                                    int resultCode, Intent data);

  /**
   * Доставка локальных сообщений через Handler/Bro/Напрямую
   * @param msgType
   * @param obj
   */
  void  onMessageToMainView(int msgType, Object obj);

  /**
   * Идентификация окон с прицелом на существование нескольких
   * экземпляров одного класса, в будущем ключ можно
   * будет расширить:
   */
  FragmentKey getFragmentKey();

  /**
   * Что делать если нажать на большую кнопку в правом углу
   */
  void  onFABclick();
}